# Add to config.py
import os

class Config:
    # Existing settings
    NLP_MODEL = os.getenv("NLP_MODEL", "en_core_web_sm")  # Environment variable fallback
    UPLOAD_FOLDER = os.path.join(os.path.abspath(os.path.dirname(__file__)), 'uploads')
    ALLOWED_EXTENSIONS = {'pdf', 'docx', 'txt'}