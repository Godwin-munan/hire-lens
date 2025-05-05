from flask import Flask
from .config import Config
from .services.nlp_processor import NLPProcessor
import os  # Add this import
import logging
from logging.handlers import RotatingFileHandler

def create_app():
    
    app = Flask(__name__)
    
    # Configure logging
    #if app.config['FLASK_ENV'] == 'development':

        
    handler = RotatingFileHandler('app.log', maxBytes=10000, backupCount=3)
    handler.setFormatter(logging.Formatter(
        '[%(asctime)s] %(levelname)s in %(module)s: %(message)s'
    ))
    app.logger.addHandler(handler)
    app.logger.setLevel(logging.INFO)
    
    app.config.from_object(Config)
    
    # Initialize NLP processor
    app.nlp_processor = NLPProcessor()
    
    # Register blueprints
    from .routes.api import bp as api_bp
    app.register_blueprint(api_bp)
    
    return app