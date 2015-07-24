import flask

import validators
from app import app


@app.errorhandler(validators.ValidationException)
def handle_validation_exception(exception):
    errors = []
    for message in exception.messages:
        errors.append({'message': message})
    response = flask.jsonify({'errors': errors})
    response.status_code = exception.status_code
    return response