# Uses Git-ignored local credentials. Run from the project root.
$ErrorActionPreference = 'Stop'
if (-not (Test-Path -LiteralPath '.env.postgres')) {
    throw 'Create .env.postgres with JDBC_DATABASE_URL, MIGRATION_DATABASE_URL, DB_USER and DB_PASSWORD first.'
}
Get-Content -LiteralPath '.env.postgres' | ForEach-Object {
    if ($_ -and -not $_.StartsWith('#')) {
        $pair = $_ -split '=', 2
        [Environment]::SetEnvironmentVariable($pair[0], $pair[1], 'Process')
    }
}
$env:SPRING_PROFILES_ACTIVE = 'postgres'
# Only for local HTTP. The hosted postgres profile keeps Secure cookies enabled.
$env:SERVER_SERVLET_SESSION_COOKIE_SECURE = 'false'
& .\mvnw.cmd spring-boot:run
