# Layout selected for Bharath's deployment

Repository: https://github.com/bharathgudla20/Java_Learning

Branch: launchpad. The existing main branch is not changed.

The project is uploaded inside job-application-tracker/. The repository's original
learning files remain in place. The root .github/workflows/launchpad-verify.yml runs
Maven with job-application-tracker as its working directory. The workflow template
inside the project folder is for a standalone copy of the project.

For Render's Docker service use:

```text
Repository: https://github.com/bharathgudla20/Java_Learning
Branch: launchpad
Dockerfile path: job-application-tracker/Dockerfile
Docker build context: job-application-tracker
Plan: Free
SPRING_PROFILES_ACTIVE: prod
```

If using the dashboard's Root Directory setting instead, set it to job-application-tracker
and use ./Dockerfile relative to that directory. Do not apply the subfolder twice.
For a Blueprint, select job-application-tracker/render.yaml as its configuration file;
that file sets rootDir for this repository layout.

If you later move the app to a dedicated repository root, remove rootDir from render.yaml
and use the local .github/workflows/verify.yml without the subfolder working directory.

Database secrets are entered through provider environment settings, not tracked files.
