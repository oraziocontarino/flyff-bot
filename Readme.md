# Flyff-Bot

This is a simple Flyff bot that allows to automate key-pressing.
It requires Java SE 11 (or newer) to be installed in OS, it can be downloaded from official site:
https://www.oracle.com/it/java/technologies/javase/jdk11-archive-downloads.html
Features:
- Auto key press: Ctrl + {0 to 9}, Alt + {0 - 9}, {0 to 9} keys
- Custom Action Slot (CAS): Configure a list of key (and cast time) of skills to execute sequentially
- Multi client: can handle up to 3 client simultaneously
- Auto save configuration: bot configuration is stored locally, opening again will re-set last configuration
- Global Hotkeys: Easy and fast way to add/remove new window handlers (bot per specific flyff window)
- Pipe Hotkeys: Easy and fast way to activate bot features in given pipe

Tested on x32 and x64 Chrome and Mozilla Firefox
- Chrome: requires window to be active to process key events
- Firefox: works 100% in background

Global HotKeys:
- {Alt + A}: Add pipeline to process another Flyff window simultaneously
- {Alt + D}: Remove last pipeline

Pipe HotKeys:
- {Shift + 1}: Pause/Resume pause all action handled by pipe 1
- {Shift + 2}: Use Custom Action Slot (CAS) in window handled by pipe 1
- {Shift + 3}: Pause/Resume pause all action handled by pipe 2
- {Shift + 4}: Use Custom Action Slot (CAS) in window handled by pipe 2
- {Shift + 5}: Pause/Resume pause all action handled by pipe 3
- {Shift + 6}: Use Custom Action Slot (CAS) in window handled by pipe 3

## Planned features:
- Auto-refresh follow
- Auto-detect Captcha (only click on box)
- Randimize delays between actions to avoid RECAPTCHA popup

## Build Native API for Windows (command line)
- Install GCC MinGW64
- Run build.bat inside NativeAPI/Windows folder

## Build & Run/Debug Native API for Windows (VS Code guide)
- Install GCC MinGW64
- Create launch.json file inside .vscode folder
- Pase following configuration:
```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "name": "g++.exe build and debug active file",
      "type": "cppdbg",
      "request": "launch",
      "program": "${fileDirname}\\${fileBasenameNoExtension}.exe",
      "args": ["api=get-window-list", "window-search-key='Flyff'"],
      //"args": ["api=send-key-down", "selected-hwnd-id=0x61060a", "keystroke-id=0x31", "keystroke-id=0x32"],
      "stopAtEntry": false,
      "cwd": "${workspaceFolder}",
      "environment": [],
      "externalConsole": false, //set to true to see output in cmd instead
      "MIMode": "gdb",
      "miDebuggerPath": "PATH_TO_DEBUGGER_EXE",//Ex: "C:\\MinGW64\\bin\\gdb.exe",
      "setupCommands": [
        {
          "description": "Enable pretty-printing for gdb",
          "text": "-enable-pretty-printing",
          "ignoreFailures": true
        }
      ],
      "preLaunchTask": "g++.exe build active file"
    },
    {
      "name": "g++ build & run active file",
      "type": "cppdbg",
      "request": "launch",
      "program": "${fileDirname}\\${fileBasenameNoExtension}.exe",
      "args": [],
      "stopAtEntry": false,
      "cwd": "${workspaceFolder}",
      "environment": [],
      "externalConsole": false, //set to true to see output in cmd instead
      "MIMode": "gdb",
      "miDebuggerPath": "PATH_TO_DEBUGGER_EXE",//Ex: "C:\\MinGW64\\bin\\gdb.exe",
      "setupCommands": [
        {
          "description": "Enable pretty-printing for gdb",
          "text": "-enable-pretty-printing",
          "ignoreFailures": true
        }
      ],
      "preLaunchTask": "g++ build & run active file"
    }
    ]
}
```
- Edit json field 'miDebuggerPath' in each section (run, debug) with the path of mingw debugger exe file
- Build & Run main.cpp

At this point you should have a file named 'main.exe' in the same directory of main.cpp.
This file is the one used by the java client to send "fetch-windows-list" and "send-key-stroke" commands to Flyff window

## Build & Run Java client
- Generate native api executable (main.exe)
- Copy/paste it to JavaClient/resources (overwrite if needed)
- Run Application.java
## Create Java executable jar
- Via maven run: mvn clean package
- Go to target folder and run JavaClient-Vx.y.z to start using FlyffBot
