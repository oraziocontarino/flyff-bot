
#include <GUIConstantsEx.au3>
#include <WinAPISysWin.au3>
#include <WindowsConstants.au3>
#include <GUIConstantsEx.au3>
#Include <GuiComboBox.au3>
#include "./Dto/Field.au3"
#include "./Dto/HashMap.au3"
#include <./Pipe/PipePanel.au3>
#include <./Pipe/PipePostMessage.au3>
#include <./Pipe/PipeEvents.au3>
Global $TITLE = "FlyFF Bot"
Global $VERSION = "0.2.0"
Global $MAX_ROWS = 14
Global $MAX_WIDTH = 300
Global $MAX_HEIGHT = 400
Global $PIPE_LIST[3]

HotKeySet("+!x", "togglePause") ; Shift-Alt-x
HotKeySet("+!c", "runActionSlot") ; Shift-Alt-c
HotKeySet("+!z", "test") ; Shift-Alt-z
AutoItSetOption ("GUIDataSeparatorChar", $DELIMITER)

Func togglePause()
  ConsoleWrite("Shift alt d pressed!" & @CRLF)
  For $i = 0 To UBound($PIPE_LIST) - 1 Step 1
    PipeEvent_onTogglePause($PIPE_LIST[$i])
  Next
EndFunc

Func runActionSlot()
  ConsoleWrite("Shift alt c pressed!" & @CRLF)
  For $i = 0 To UBound($PIPE_LIST) - 1 Step 1
    PipeEvent_onActionSlot($PIPE_LIST[$i])
  Next
EndFunc

Func test()
  ConsoleWrite("Shift alt z pressed!" & @CRLF)
  Pipe_remove($PIPE_LIST[0])
EndFunc

Func addPipe(ByRef $pipeInitData)
  For $i = 0 To UBound($PIPE_LIST) - 1 Step 1
    If $PIPE_LIST[$i] <> Null Then
      ConsoleWrite("Add separator to pipe: " & $i & @CRLF)
      Pipe_addPipeSeparator($PIPE_LIST[$i])
    Else

      Local $offsetX = 8 + (($MAX_WIDTH+2) * $i)
      ConsoleWrite("Add pipe: " & $i & " with offset: " & $offsetX & @CRLF)
      HashMap_putFieldRaw($pipeInitData, "offsetX", $offsetX)
      $PIPE_LIST[$i] = Pipe_initPipe($pipeInitData) ; Create pipe $i
      return
    EndIf
  Next
EndFunc

Func processPipeEvents($guiMessage, $delta)
  For $i = 0 To UBound($PIPE_LIST) - 1 Step 1
    PipeEvent_onWindowNameChange($PIPE_LIST[$i], $guiMessage)
    PipeEvent_onConfigurationChange($PIPE_LIST[$i], $guiMessage)
    PipeEvent_onStatusChange($PIPE_LIST[$i], $guiMessage)
    PipeEvent_onRefreshWindowList($PIPE_LIST[$i])
    If $delta > 500 Then
      PipeEvent_onActionSchedule($PIPE_LIST[$i])
    EndIf
  Next
EndFunc

Func createGui()
  ; Create a GUI with various controls.
  Local $fullTitle = $TITLE & " v" & $VERSION
  Local $rowHeight = 25
  Local $padding = 4
  ConsoleWrite("WINDOW-SIZE --> WINDOW: " & (($MAX_WIDTH+8)*3 - 8) & "px " & @CRLF)
  Local $hGUI = GUICreate($fullTitle, ($MAX_WIDTH+2)*3, $MAX_ROWS*($rowHeight+$padding))

  For $i = 0 To UBound($PIPE_LIST) - 1 Step 1
    $PIPE_LIST[$i] = Null
  Next
  Local $pipeInitData = HashMap_init(25)
  HashMap_putFieldRaw($pipeInitData, "rowHeight", $rowHeight)
  HashMap_putFieldRaw($pipeInitData, "padding", $padding)
  HashMap_putFieldRaw($pipeInitData, "maxWidth", $MAX_WIDTH)
  HashMap_putFieldRaw($pipeInitData, "maxHeight", $MAX_HEIGHT)

  addPipe($pipeInitData) ; Create pipe 1
  addPipe($pipeInitData) ; Create pipe 2
  addPipe($pipeInitData) ; Create pipe 3

  ; Display the GUI.
  GUISetState(@SW_SHOW, $hGUI)

  Local $begin = TimerInit()
  ; Loop until the user exits.
  While 1
    Local $guiMessage = GUIGetMsg()

    If $guiMessage = $GUI_EVENT_CLOSE Then
      ExitLoop
    EndIf

    Local $delta = TimerDiff($begin)
    processPipeEvents($guiMessage, $delta)

    If $delta > 500 Then
      $begin = TimerInit()
    EndIf
  WEnd

  ; Delete the previous GUI and all controls.
  GUIDelete($hGUI)
EndFunc   ;==>Example

ConsoleWrite("Start - FlyffBot GUI" & @CRLF)
createGui()
ConsoleWrite("End - FlyffBot GUI" & @CRLF)
