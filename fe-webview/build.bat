.\venv\Scripts\pyinstaller.exe --onefile -n FlyffBot main.py
cd ..
md release
move /Y .\fe-webview\dist\FlyffBot.exe .\release
cd .\fe-webview
