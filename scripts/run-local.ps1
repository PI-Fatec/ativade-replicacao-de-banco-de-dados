$env:PRIMARY_URL="jdbc:mysql://localhost:3306/aula-db"
$env:PRIMARY_USER="root"
$env:PRIMARY_PASSWORD="root"

$env:REPLICA_URLS=""
$env:GROUP_NAME="NomeDoGrupo"

# Compilar
Write-Host "Compilando código..."
if (!(Test-Path -Path "bin")) { New-Item -ItemType Directory -Force -Path "bin" | Out-Null }
javac -d bin -cp "libs/*;src" (Get-ChildItem -Path "src" -Recurse -Filter "*.java").FullName

# Executar
Write-Host "Executando demonstração..."
java -cp "libs/*;bin" br.com.grupo.Main
