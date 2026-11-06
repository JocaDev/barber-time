# Como compilar e executar

Requer JDK 17 ou superior. Rode na pasta do projeto.

Linux/Mac:
```bash
mkdir out
javac -encoding UTF-8 -d out $(find src/main -name "*.java")
java -Dfile.encoding=UTF-8 -cp out Main 48110-000
```

Windows (PowerShell):
```powershell
mkdir out
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse -Filter *.java src/main | % FullName)
java -Dfile.encoding=UTF-8 -cp out Main 48110-000
```

Teste isolado do ViaCEP:
```bash
javac -encoding UTF-8 -cp out -d out src/test/ViaCepClientTeste.java
java -Dfile.encoding=UTF-8 -cp out ViaCepClientTeste
```
