from flask import Blueprint, request, jsonify, current_app
from src.services.nlp_processor import NLPProcessor
import json

bp = Blueprint('api', __name__)

@bp.route('/parse', methods=['POST'])
def parse_resume():
    # Log incoming request
    request_data = {
        'method': request.method,
        'path': request.path,
        'content_type': request.content_type,
        'content_length': request.content_length
    }
    current_app.logger.info("Incoming request", extra={"request": request_data})
    
    try:
        text = request.json.get('text', '')
            
            
        # Security-conscious logging
        current_app.logger.info(f"Received text payload (length: {len(text)} characters)")
    
        # Debug logging (truncated preview)
        current_app.logger.debug(f"Text preview: {text[:100]}{'...' if len(text) > 100 else ''}")
    
        if not text:
            current_app.logger.warning("Empty text payload received")
            return jsonify({"error": "No text provided"}), 400
            
        # Log sanitized input
        current_app.logger.debug(f"Processing text: {text[:100]}...")
        
        processor = NLPProcessor()
        results = processor.parse_resume(text)  # Properly indented
        
        # Log results summary
        log_results = {
            "skills_found": len(results.get('skills', [])),
            "experience_items": len(results.get('experience', []))
        }
        current_app.logger.info("Processing completed", extra={"results_summary": log_results})
        
        # Debug logging
        current_app.logger.debug(f"Full results: {json.dumps(results, indent=2)}")
        
        return jsonify(results)  # Correct final return
        
    except Exception as e:
        current_app.logger.error("Processing failed", exc_info=True)
        return jsonify({"error": "Internal server error"}), 500  # Fixed typo