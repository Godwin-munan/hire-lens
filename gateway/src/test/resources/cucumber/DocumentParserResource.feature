Feature: File Upload and Information Extraction

  Scenario: Upload a valid PDF file and extract person data
    Given I have a valid "resume.pdf" file for person extraction
    When I upload the file to "/api/doc/upload"
    Then the response should contain extracted person "person"

  Scenario: Upload a valid PDF file and extract skills data
    Given I have a valid "resume.pdf" file for skill extraction
    When I upload the file to "/api/doc/upload"
    Then the response should contain extracted skills "skills"

#  Scenario: Upload an invalid file type
#    Given I have an invalid "resume.txt" file
#    When I upload the file to "/api/doc/upload"
#    When I upload the file to "http://localhost:10344/api/doc/upload"
#    Then the response should contain an error message "Invalid file type"

  Scenario: Upload an empty file
    Given I have an empty file
    When I upload the file to "/api/doc/upload"
    Then the response should contain an error message "Oops! File is empty, please check."
