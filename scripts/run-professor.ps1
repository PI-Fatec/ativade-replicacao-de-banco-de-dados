$env:PRIMARY_URL="jdbc:mysql://IP_PRIMARIO:3306/aula-db"
$env:PRIMARY_USER="usuario"
$env:PRIMARY_PASSWORD="senha"

$env:REPLICA_URLS="jdbc:mysql://IP_REPLICA_1:3306/aula-db,jdbc:mysql://IP_REPLICA_2:3306/aula-db"
$env:GROUP_NAME="NomeDoGrupo"

# Compilar
Write-Host "Compilando código..."
if (!(Test-Path -Path "bin")) { New-Item -ItemType Directory -Force -Path "bin" | Out-Null }
javac -d bin -cp "libs/*;src" (Get-ChildItem -Path "src" -Recurse -Filter "*.java").FullName

# Executar
Write-Host "Executando demonstração..."
java -cp "libs/*;bin" br.com.grupo.Main
