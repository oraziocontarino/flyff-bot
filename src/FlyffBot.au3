
#include <GUIConstantsEx.au3>
#include <WinAPISysWin.au3>
#include <WindowsConstants.au3>
#include <GUIConstantsEx.au3>
#Include <GuiComboBox.au3>

#include <./Logger/Logger.au3>
#include <./Dto/Field.au3>
#include <./Dto/HashMap.au3>

#Include <./Gui/Gui.au3>
#include <./Pipe/PipePanel.au3>
#include <./Pipe/PipePostMessage.au3>
#include <./Pipe/PipeEvents.au3>
Global $TITLE = "FlyFF Bot"
Global $VERSION = "0.3.0"
Global $MAX_ROWS = 14
Global $MAX_WIDTH = 300
Global $MAX_HEIGHT = 400
Global $MAX_PIPES = 3
Global $PIPE_LIST[$MAX_PIPES]
Global $VISIBLE_PIPE = 0
Global $rowHeight = 25
Global $padding = 4
Global $hGUI = Null
Global $pipeInitData = HashMap_init(25)
Global $editGuiLock = false

HotKeySet("+!x", "togglePause") ; Shift-Alt-x
HotKeySet("+!c", "runActionSlot") ; Shift-Alt-c
HotKeySet("!a", "addPipe") ; Alt + A
HotKeySet("!d", "removePipe") ; Alt + D
HotKeySet("!t", "test") ; Alt + T
AutoItSetOption ("GUIDataSeparatorChar", $DELIMITER)

Func test()
EndFunc

Func getGuiPos()
  Local $list = WinList("[REGEXPTITLE:(?i)(Flyff Bot*)]")
  return WinGetPos($list[1][0]);Get current window x, y
EndFunc

Func togglePause()
  println("Shift alt x pressed!")
  For $i = 0 To UBound($PIPE_LIST) - 1 Step 1
    If $PIPE_LIST[$i] <> Null Then
      PipeEvent_onTogglePause($PIPE_LIST[$i])
    EndIf
  Next
EndFunc

Func runActionSlot()
  println("Shift alt c pressed!")
  For $i = 0 To UBound($PIPE_LIST) - 1 Step 1
    If $PIPE_LIST[$i] <> Null Then
      PipeEvent_onActionSlot($PIPE_LIST[$i])
    EndIf
  Next
EndFunc



Func calculateGuiWidth()
  return ($MAX_WIDTH+8)*$VISIBLE_PIPE
EndFunc

Func calculateGuiHeight()
  return $MAX_ROWS*($rowHeight+$padding)
EndFunc

Func addPipe()
  If $editGuiLock Then
    return
  EndIf

  If $VISIBLE_PIPE >= $MAX_PIPES Or $editGuiLock Then
    return
  EndIf
  $editGuiLock = true
  $VISIBLE_PIPE = $VISIBLE_PIPE + 1

  Local $i = ($VISIBLE_PIPE-1)
  Local $offsetX = 8 + (($MAX_WIDTH+2) * $i)
  HashMap_putFieldRaw($pipeInitData, "offsetX", $offsetX, $TYPE_NUMBER)
  $PIPE_LIST[$i] = Pipe_initPipe($pipeInitData, $i) ; Create pipe $i

  if $i > 0 Then
    Pipe_addPipeSeparator($PIPE_LIST[$i-1])
  EndIf

  Local $pos = getGuiPos()
  WinMove($hGUI, "", $pos[0], $pos[1], calculateGuiWidth(), calculateGuiHeight())
  $editGuiLock = false
EndFunc

Func removePipe()
  If $editGuiLock Then
    return
  EndIf

  If $VISIBLE_PIPE <= 1 Then
    return
  EndIf
  $editGuiLock = true
  $VISIBLE_PIPE = $VISIBLE_PIPE - 1

  PipeEvent_publishDeleteEvent($PIPE_LIST[$VISIBLE_PIPE])
EndFunc

Func processPipeEvents($guiMessage, $delta)
  For $i = 0 To UBound($PIPE_LIST) - 1 Step 1
    Local $pipe = $PIPE_LIST[$i]
    If $pipe = Null Then
      ContinueLoop
    EndIf

    PipeEvent_onWindowNameChange($PIPE_LIST[$i], $guiMessage)
    PipeEvent_onConfigurationChange($PIPE_LIST[$i], $guiMessage)
    PipeEvent_onStatusChange($PIPE_LIST[$i], $guiMessage)
    PipeEvent_onRefreshWindowList($PIPE_LIST[$i])

    If $delta < 500 Then
      ContinueLoop
    EndIf
    PipeEvent_onActionSchedule($PIPE_LIST[$i])
    Local $removed = PipeEvent_processDeleteEvent($PIPE_LIST[$i])
    if $removed Then
      $PIPE_LIST[$i] = Null
      Local $pos = getGuiPos()
      WinMove($hGUI, "", $pos[0], $pos[1], calculateGuiWidth(), calculateGuiHeight())
      $editGuiLock = false
    EndIf
    if $removed AND $i > 0 Then
      Pipe_removePipeSeparator($PIPE_LIST[$i-1])
    EndIf
  Next
EndFunc

Func createGui()
  ; Create a GUI with various controls.
  Local $fullTitle = $TITLE & " v" & $VERSION
  $hGUI = GUICreate($fullTitle, calculateGuiWidth(), calculateGuiHeight())
  For $i = 0 To UBound($PIPE_LIST) - 1 Step 1
    $PIPE_LIST[$i] = Null
  Next

  HashMap_putFieldRaw($pipeInitData, "rowHeight", $rowHeight, $TYPE_NUMBER)
  HashMap_putFieldRaw($pipeInitData, "padding", $padding, $TYPE_NUMBER)
  HashMap_putFieldRaw($pipeInitData, "maxWidth", $MAX_WIDTH, $TYPE_NUMBER)
  HashMap_putFieldRaw($pipeInitData, "maxHeight", $MAX_HEIGHT, $TYPE_NUMBER)

  addPipe() ; Create pipe 1

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

println("Start - FlyffBot GUI")
createGui()
println("End - FlyffBot GUI")
