-- Run in H2 Console after adding applications. SELECT queries do not change your data.
SELECT * FROM job_applications ORDER BY applied_on DESC, id DESC;

SELECT company, role, status FROM job_applications WHERE status = 'INTERVIEW';

SELECT * FROM job_applications WHERE LOWER(company) LIKE '%acme%';

SELECT status, COUNT(*) AS total FROM job_applications GROUP BY status;

SELECT company, COUNT(*) AS total FROM job_applications
GROUP BY company HAVING COUNT(*) > 1 ORDER BY total DESC;

SELECT MIN(applied_on) AS first_application, MAX(applied_on) AS latest_application
FROM job_applications;

-- Write exercises (replace the ID with an existing test record; uncomment to run):
-- UPDATE job_applications SET status = 'INTERVIEW' WHERE id = 1;
-- DELETE FROM job_applications WHERE id = 1;

-- Stretch exercise: add an interview_rounds table with an application_id foreign key.
-- Then use JOIN to list each company and its interview rounds.
-- This first version deliberately has one table; no relationship/JOIN feature is implemented yet.
