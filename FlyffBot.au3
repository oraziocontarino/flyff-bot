#include <WinAPISysWin.au3>
#include <WindowsConstants.au3>
#include <GUIConstantsEx.au3>

Global $TITLE = "FlyFF Bot"
Global $VERSION = "0.2.0"
Global $MAX_WIDTH = 300
Global $MAX_ROWS = 14
Global $DELIMITER = "►"

Global $maxConfigurations = 10
Global $configurations[$maxConfigurations]
Global $keyPrefix = "Key: "
Global $keyAltPrefix = "Alt + "
Global $keyCtrlPrefix = "Ctrl + "
Global $keyDownTime = 1000
Global $rowHeight = 25
Global $padding = 4 ; Used to separate pair element (label,input) from next ones. Also used to separate rows
Global $paddingSm = 2 ;Used to separate label from input in a row
Global $windowNameRegex = ".*Flyff.*Universe.*" ; Regex used to get the list of flyff windows
Global $windowName = ""
Global $pipe1[6]
Global $hwnd

AutoItSetOption ("GUIDataSeparatorChar", $DELIMITER)
HotKeySet("+!x", "togglePausePipe1") ; Shift-Alt-x
HotKeySet("+!c", "runActionSlot") ; Shift-Alt-c
;HotKeySet("+!n", "_debug_logMousePost") ; Shift-Alt-n

;Func _debug_logMousePost()
;  leftClick(3500, 980)
;  ;ConsoleWrite("Clicked!" & @CRLF)
;EndFunc

Func leftClick($x, $y)
  $hwnd = WinGetHandle($windowName)
  ControlClick($hwnd, "", "", "left", 10, $x, $y)
  ;MouseClick($MOUSE_CLICK_LEFT, $x, $y, 2)
EndFunc

Func togglePausePipe1()
  $pipe1[5] = Not $pipe1[5] ; Toggle Pause pipe
  ;ConsoleWrite("Shift alt d pressed!" & @CRLF)
EndFunc

Func runActionSlot()
  sendKeyDown(retrieveKeyCode("c"))
  sendKeyUp(retrieveKeyCode("c"))
EndFunc

Func sendKeyDown($keyData)
  For $i = 1 To $keyData[0] Step 1
    Local $keyCode = $keyData[$i]
    ;ConsoleWrite("Sending KEYDOWN " & $keyCode & @CRLF)
    _WinAPI_PostMessage($hwnd, $WM_KEYDOWN, $keyCode, 0)
  Next
EndFunc

Func sendKeyUp($keyData)
  For $i = 1 To $keyData[0] Step 1
    Local $keyCode = $keyData[$i]
    ;ConsoleWrite("Sending KEYUP " & $keyCode & @CRLF)
    _WinAPI_PostMessage($hwnd, $WM_KEYUP, $keyCode, 0)
  Next
EndFunc


Func buildKeyData($maxKeysDown, $keyCode1, $keyCode2)
  Local $keyData[$maxKeysDown + 1]

  $keyData[1] = $keyCode1
  If $keyCode2 <> Null Then
    $keyData[0] = 2 ;size of keys to press
    $keyData[2] = $keyCode2
  Else
    $keyData[0] = 1 ;size of keys to press
  EndIf
  return $keyData
EndFunc

Func retrieveKeyCode($rawKey)
  ;$key_1 = 0x61 ; Numpad-1 key, see https://docs.microsoft.com/en-us/windows/desktop/inputdev/virtual-key-codes
  Local $maxKeysDown = 9

  Local $base = 0x60
  Local $ctrlKey = 0x11
  Local $altKey = 0x12
  local $cKey = 0x43

  For $i = 0 To $maxKeysDown Step 1
    Local $baseKey = $keyPrefix & $i
    Local $altBaseKey = $keyAltPrefix & $i
    Local $ctrlBaseKey = $keyCtrlPrefix & $i

    If $rawKey = $baseKey Then
      return buildKeyData($maxKeysDown, $base + $i, Null)
    ElseIf $rawKey = $ctrlBaseKey Then
      return buildKeyData($maxKeysDown, $ctrlKey, $base + $i)
    ElseIf $rawKey = $altBaseKey Then
      return buildKeyData($maxKeysDown, $altKey, $base + $i)
    EndIf
  Next

  Switch $rawKey
    Case "c"
      return buildKeyData($maxKeysDown, $cKey, Null)
  EndSwitch
EndFunc

Func scheduleActions()
  $hwnd = WinGetHandle($windowName)

  For $i = 0 to UBound($configurations) -1
    Local $action = $configurations[$i]
    If Not $action[5] Then
      ContinueLoop
    EndIf

    Local $lastProcessed = 0
    Local $delta = TimerDiff($action[6])
    Local $timeToAwait = ($action[3] * 1000)
    If $delta > $timeToAwait Then
      $action[6] = TimerInit()
      $configurations[$i] = $action
      Local $message = "Process action: " & _
        "{" & _
        " combo: '" & $action[1] & "'," & _
        " delay: '" & $action[3] & "'," & _
        " active: '" & $action[5] & "'," & _
        " time: '" & $action[6] & "'" & _
        " timeToAwait: '" & $timeToAwait & "'" & _
        " timePassed: '" & $delta & "'" & _
        "}" & @CRLF
      ConsoleWrite($message)
      sendKeyDown(retrieveKeyCode($action[1]))
      sendKeyUp(retrieveKeyCode($action[1]))
    EndIf
  Next
EndFunc

Func generateKeyLabelsForCombo()
  Local $allKeys = ""

  For $i = 1 To 9 Step +1
    $allKeys = $allKeys & $DELIMITER & $keyPrefix & $i
  Next

  For $i = 1 To 9 Step +1
    $allKeys = $allKeys & $DELIMITER & $keyAltPrefix & $i
  Next

  For $i = 1 To 9 Step +1
    $allKeys = $allKeys & $DELIMITER & $keyCtrlPrefix & $i
  Next
  return StringTrimLeft($allKeys, 1)
EndFunc

Func addBindingRow($rowIndex, ByRef $nextRowId)
  Local $xPos = 0
  Local $yPos = 0
  Local $width = 0

  ; Increment row - increment height, reset width
  $nextRowId = $nextRowId + 1
  $yPos = calculateNextRowYPos($nextRowId)
  $xPos = 8

  ; Key list - Label
  $width=18
  Local $keyLabel = GUICtrlCreateLabel("Key:", $xPos, $yPos, $width)
  $xPos = $xPos + $width + $paddingSm

  ; Key list - Combo
  $width=75
  Local $keys = generateKeyLabelsForCombo()
  Local $defaultKey = $keyPrefix & $rowIndex
  Local $combo = GUICtrlCreateCombo($defaultKey, $xPos, $yPos, $width)
  GUICtrlSetData($combo, $keys, $defaultKey)
  $xPos = $xPos + $width + $padding

  ; Delay - Label
  $width = 45
  Local $delayLabel = GUICtrlCreateLabel("Delay (s):", $xPos, $yPos, $width)
  $xPos = $xPos + $width + $paddingSm

  ; Delay - Input
  Local $width = 25
  Local $delayInput = GUICtrlCreateInput("1", $xPos, $yPos, $width)
  $xPos = $xPos + $width + $padding

  ; Enabled - Checkbox
  $width = 50
  Local $enabled = GUICtrlCreateCheckbox("Active", $xPos, $yPos, $width)
  $xPos = $xPos + $width + $padding

  ; Prepare output
  Local $out[7]
  $out[0] = $combo
  $out[1] = GUICtrlRead($combo)
  $out[2] = $delayInput
  $out[3] = GUICtrlRead($delayInput)
  $out[4] = $enabled
  $out[5] = ((GUICtrlRead($enabled)) == $GUI_CHECKED)
  $out[6] = TimerInit() ; Initial last time invoked value
  return $out
EndFunc

Func calculateNextRowYPos($nextRowId)
  Local $yPos = ($nextRowId * ($rowHeight + $padding))
  return $yPos
EndFunc

Func buildWindowListComboItems()
  Local $aWinList = WinList("[REGEXPTITLE:(?i)(" & $windowNameRegex & ")]")
  Local $out[2]
  $out[1] = ""

  For $i = 1 To $aWinList[0][0] Step 1
    Local $item = $aWinList[$i][0]
    If $i = 1 Then
      $out[0] = $item
    EndIf
    $out[1] = $out[1] & $DELIMITER & $item
  Next

  $out[1] = StringTrimLeft($out[1], 1)

  return $out
EndFunc

Func createGui()
  ; Create a GUI with various controls.
  Local $fullTitle = $TITLE & " v" & $VERSION
  Local $hGUI = GUICreate($fullTitle, $MAX_WIDTH, $MAX_ROWS*($rowHeight+$padding))

  Local $xPos = 8
  Local $yPos = 8
  Local $width = 0
  Local $nextRowId = 0

  ; Window Name - Label
  ; Debug
  $width = 80
  Local $windowNameLabel = GUICtrlCreateLabel("Window Name:", $xPos, $yPos, $width)
  $xPos = $xPos + $width + $paddingSm

  ; Window Name - Input
  $width = 200
  Local $windowComboItems = buildWindowListComboItems()
  Local $windowNameInput = GUICtrlCreateCombo($windowComboItems[0], $xPos, $yPos, $width)
  GUICtrlSetData($windowNameInput, $windowComboItems[1], $windowName)
  $xPos = $xPos + $width + $padding

  ; Increment row - increment height, reset width
  $nextRowId = $nextRowId + 1
  $yPos = calculateNextRowYPos($nextRowId)
  $xPos = 8

  ; Pipeline 1 Header - Label
  $width = $MAX_WIDTH - $padding
  Local $pipeStatus[3]
  $pipeStatus[0] = "IDLE_NO_ACTIONS"
  $pipeStatus[1] = "PAUSED"
  $pipeStatus[2] = "RUNNING"

  $pipe1[0] = GUICtrlCreateLabel("Bot status: Idle - No active actions", $xPos, $yPos, $width)
  $pipe1[1] = GUICtrlCreateLabel("Bot status: Paused - (Alt+Shift+x)", $xPos, $yPos, $width)
  $pipe1[2] = GUICtrlCreateLabel("Bot status: Running... - (Alt+Shift+x)", $xPos, $yPos, $width)
  $pipe1[3] = 0 ; Active actions counter
  $pipe1[4] = "IDLE_NO_ACTIONS" ; Last status
  $pipe1[5] = false ; Force paused
  GUICtrlSetState($pipe1[1], $GUI_HIDE)
  GUICtrlSetState($pipe1[2], $GUI_HIDE)

  ; Increment row - increment height, reset width
  $nextRowId = $nextRowId + 1
  $yPos = calculateNextRowYPos($nextRowId)
  $xPos = 8

  ; HotKey 1 Info
  GUICtrlCreateLabel("Toggle Pause: Shift+Alt+X", $xPos, $yPos, $width)

  ; Increment row - increment height, reset width
  $nextRowId = $nextRowId + 1
  $yPos = calculateNextRowYPos($nextRowId)
  $xPos = 8

  ; HotKey 1 Info
  GUICtrlCreateLabel("Use ActionSlot: Shift+Alt+C", $xPos, $yPos, $width)

  For $i = 0 To ($maxConfigurations - 1) Step 1
    $configurations[$i] = addBindingRow($i, $nextRowId)
  Next

  ; Display the GUI.
  GUISetState(@SW_SHOW, $hGUI)

  Local $begin = TimerInit()
  ; Loop until the user exits.
  While 1
    ;_debug_logMousePost()
    Local $guiMessage = GUIGetMsg()
    Switch $guiMessage
      Case $GUI_EVENT_CLOSE
        ExitLoop
      Case $windowNameInput
        $windowName = GUICtrlRead($windowNameInput)
        ConsoleWrite("Updated window name to: " & $windowName & @CRLF)
    EndSwitch

    For $i = 0 To UBound($configurations) - 1
      Local $action = $configurations[$i]
      Switch $guiMessage
        Case $action[0]
          $action[1] = GUICtrlRead($action[0])
          ConsoleWrite("Updated combo value to: " & $action[1]  & @CRLF)
        Case $action[2]
          $action[3] = GUICtrlRead($action[2])
          ConsoleWrite("Updated delay value to: " & $action[3]  & @CRLF)
        Case $action[4]
          $action[5] = ((GUICtrlRead($action[4])) == $GUI_CHECKED)
          $action[6] = TimerInit()
          ConsoleWrite("Updated active value to: " & $action[5] & @CRLF)
          If $action[5] Then
            $pipe1[3] = $pipe1[3] + 1
          Else
            $pipe1[3] = $pipe1[3] - 1
          EndIf
      EndSwitch
      $configurations[$i] = $action
    Next

    Local $hasActionEnabled = $pipe1[3] > 0
    Local $isPaused = $pipe1[5]
    Switch($pipe1[4]) ; Read current pipe state
      Case $pipeStatus[0] ; Current status: IDLE
        If $hasActionEnabled And (Not $isPaused) Then
          GUICtrlSetState($pipe1[0], $GUI_HIDE) ; Hide Idle label
          GUICtrlSetState($pipe1[1], $GUI_HIDE) ; Hide Paused label
          GUICtrlSetState($pipe1[2], $GUI_SHOW) ; Show Running label
          $pipe1[4] = $pipeStatus[2] ; Update pipe state to: RUNNING
        ElseIf $hasActionEnabled Then
          GUICtrlSetState($pipe1[0], $GUI_HIDE) ; Hide Idle label
          GUICtrlSetState($pipe1[1], $GUI_SHOW) ; Show Paused label
          GUICtrlSetState($pipe1[2], $GUI_HIDE) ; Hide Running label
          $pipe1[4] = $pipeStatus[1] ; Update pipe state to: PAUSED
        EndIf
      Case $pipeStatus[1] ; Current status: PAUSED
        If $hasActionEnabled And (Not $isPaused) Then
          GUICtrlSetState($pipe1[0], $GUI_HIDE) ; Hide Idle label
          GUICtrlSetState($pipe1[1], $GUI_HIDE) ; Hide Paused label
          GUICtrlSetState($pipe1[2], $GUI_SHOW) ; Show Running label
          $pipe1[4] = $pipeStatus[2] ; Update pipe state to: RUNNING
        ElseIf Not $hasActionEnabled And (Not $isPaused) Then
          GUICtrlSetState($pipe1[0], $GUI_SHOW) ; Show Idle label
          GUICtrlSetState($pipe1[1], $GUI_HIDE) ; Hide Paused label
          GUICtrlSetState($pipe1[2], $GUI_HIDE) ; Hide Running label
          $pipe1[4] = $pipeStatus[0] ; Update pipe state to: IDLE
        EndIf
      Case $pipeStatus[2] ; Current status: RUNNING
        If $hasActionEnabled And $isPaused Then
          GUICtrlSetState($pipe1[0], $GUI_HIDE) ; Hide Idle label
          GUICtrlSetState($pipe1[1], $GUI_SHOW) ; Show Paused label
          GUICtrlSetState($pipe1[2], $GUI_HIDE) ; Hide Running label
          $pipe1[4] = $pipeStatus[1] ; Update pipe state to: PAUSE
        ElseIf Not $hasActionEnabled And (Not $isPaused) Then
          GUICtrlSetState($pipe1[0], $GUI_SHOW) ; Show Idle label
          GUICtrlSetState($pipe1[1], $GUI_HIDE) ; Hide Paused label
          GUICtrlSetState($pipe1[2], $GUI_HIDE) ; Hide Running label
          $pipe1[4] = $pipeStatus[0] ; Update pipe state to: IDLE
        EndIf
    EndSwitch

    $delta = TimerDiff($begin)
    If $delta > 500 And Not $pipe1[5] Then
      $begin = TimerInit()
      scheduleActions()
    EndIf
  WEnd

  ; Delete the previous GUI and all controls.
  GUIDelete($hGUI)
EndFunc   ;==>Example

ConsoleWrite("Start - FlyfBot GUI" & @CRLF)
createGui()
ConsoleWrite("End - FlyffBot GUI" & @CRLF)
