cd fe
call npm run build

cd ..

cd JavaClient
call mvn clean compile package install

cd ..

cd fe-webview
.\build.bat

cd ..
pause