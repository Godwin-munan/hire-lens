from src.config import Config
import spacy
from spacy.matcher import Matcher
from spacy.tokens import Span
from typing import Dict, List

class NLPProcessor:
    def __init__(self):
        self.nlp = self._load_model()
        self.matcher = self._initialize_matcher()

    def _load_model(self):
        """Load spaCy model with error handling"""
        try:
            nlp = spacy.load(Config.NLP_MODEL)
            return nlp
        except OSError:
            raise RuntimeError(
                f"Model {Config.NLP_MODEL} not found. "
                f"Run: python -m spacy download {Config.NLP_MODEL}"
            )

    def _initialize_matcher(self) -> Matcher:
        """Set up custom pattern matcher"""
        matcher = Matcher(self.nlp.vocab)
        
        # Job title patterns (e.g., "Senior Software Engineer")
        title_patterns = [
            [
                {"POS": "ADJ", "OP": "*"},
                {"POS": "PROPN", "OP": "+"},
                {"LOWER": {"IN": ["developer", "engineer", "analyst", "specialist"]}}
            ],
            [
                {"POS": "PROPN", "OP": "+"},
                {"LOWER": {"IN": ["manager", "director", "lead"]}}
            ]
        ]
        matcher.add("JOB_TITLE", title_patterns)

        # Skill patterns (e.g., "Proficient in Python", "Skilled with Docker")
        skill_patterns = [
            [
                {"LOWER": {"IN": ["proficient", "experienced", "skilled"]}},
                {"LOWER": {"IN": ["in", "with", "at"]}, "OP": "?"},
                {"POS": {"IN": ["NOUN", "PROPN"]}}
            ]
        ]
        matcher.add("SKILL_INDICATOR", skill_patterns)
        
        return matcher

    def parse_resume(self, text: str) -> Dict:
        """Process resume text and return structured data"""
        doc = self.nlp(text)
        return {
            "skills": self._extract_skills(doc),
            "experience": self._extract_experience(doc)
        }
    
    def _extract_skills(self, doc) -> List[str]:
        """Extract technical skills using multiple strategies"""
        skills = set()
        
        # 1. Match known technical terms
        tech_terms = {'python', 'java', 'sql', 'javascript', 'flask', 
                     'spring boot', 'docker', 'aws'}
        
        # 2. Match noun phrases with technical context
        for chunk in doc.noun_chunks:
            lower_text = chunk.text.lower()
            if any(term in lower_text for term in tech_terms):
                skills.add(chunk.text)
        
        # 3. Use custom matcher patterns
        matches = self.matcher(doc)
        for match_id, start, end in matches:
            if self.nlp.vocab.strings[match_id] == "SKILL_INDICATOR":
                skills.add(doc[start:end].text)
        
        return sorted(skills)

    def _extract_experience(self, doc) -> List[Dict]:
        """Extract work experience with date-organization relationships"""
        experience = []
        dates = [ent for ent in doc.ents if ent.label_ == "DATE"]
        orgs = [ent for ent in doc.ents if ent.label_ == "ORG"]
        
        for date in dates:
            # Find organizations within 3 sentences of date
            nearby_orgs = [
                org.text for org in orgs
                if abs(org.sent.start - date.sent.start) <= 3
            ]
            
            if nearby_orgs:
                experience.append({
                    "date": date.text,
                    "companies": list(set(nearby_orgs)),
                    "context": date.sent.text
                })
        
        return experience

    def _extract_education(self, doc) -> List[str]:
        """Extract education information with degree detection"""
        education = []
        degree_keywords = {
            'bachelor': ['bsc', 'b.eng', 'bachelor'],
            'master': ['msc', 'm.eng', 'master'],
            'phd': ['phd', 'doctorate']
        }
        
        for sent in doc.sents:
            sent_text = sent.text.lower()
            if any(edu_word in sent_text for edu_word in ['university', 'college', 'institute']):
                # Detect degree level
                degree_level = next(
                    (level for level, keywords in degree_keywords.items()
                     if any(kw in sent_text for kw in keywords)),
                    None
                )
                
                education_entry = {
                    "text": sent.text,
                    "degree_level": degree_level
                }
                education.append(education_entry)
        
        return education

    def _extract_entities(self, doc) -> Dict[str, List]:
        """Extract and categorize named entities"""
        entity_types = ["PERSON", "ORG", "GPE", "DATE", "NORP"]
        return {
            label: sorted({ent.text for ent in doc.ents if ent.label_ == label})
            for label in entity_types
        }