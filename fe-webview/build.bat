.\venv\Scripts\pyinstaller.exe --onefile --noconsole -n FlyffBot main.py
cd ..
md release
move /Y .\fe-webview\dist\FlyffBot.exe .\release