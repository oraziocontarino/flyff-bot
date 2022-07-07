#include <WinAPISysWin.au3>
#include <WindowsConstants.au3>
#include <GUIConstantsEx.au3>

Global $MAX_WIDTH = 300
Global $MAX_ROWS = 14
Global $maxConfigurations = 10
Global $configurations[$maxConfigurations]
Global $keyPrefix = "key_"
Global $keyDownTime = 1000
Global $rowHeight = 25
Global $padding = 4 ; Used to separate pair element (label,input) from next ones. Also used to separate rows
Global $paddingSm = 2 ;Used to separate label from input in a row
Global $windowName = "Flyff Universe – Mozilla Firefox"
Global $pipe1[6]
Global $hwnd

HotKeySet("+!x", "togglePausePipe1") ; Shift-Alt-x
HotKeySet("+!c", "runActionSlot") ; Shift-Alt-c

Func togglePausePipe1()
  $pipe1[5] = Not $pipe1[5] ; Toggle Pause pipe
  ConsoleWrite("Shift alt d pressed!" & @CRLF)
EndFunc

Func runActionSlot()
  sendKeyDown(retrieveKeyCode("c"))
  sendKeyUp(retrieveKeyCode("c"))
EndFunc

Func sendKeyDown($keyCode)
  ConsoleWrite("Sending KEYDOWN " & $keyCode & @CRLF)
  _WinAPI_PostMessage($hwnd, $WM_KEYDOWN, $keyCode, 0)
EndFunc

Func sendKeyUp($keyCode)
  ConsoleWrite("Sending KEYUP " & $keyCode & @CRLF)
  _WinAPI_PostMessage($hwnd, $WM_KEYUP, $keyCode, 0)
EndFunc

Func retrieveKeyCode($rawKey)
  ;$key_1 = 0x61 ; Numpad-1 key, see https://docs.microsoft.com/en-us/windows/desktop/inputdev/virtual-key-codes
  Switch $rawKey
    Case "key_0"
      return 0x60
    Case "key_1"
      return 0x61
    Case "key_2"
      return 0x62
    Case "key_3"
      return 0x63
    Case "key_4"
      return 0x64
    Case "key_5"
      return 0x65
    Case "key_6"
      return 0x66
    Case "key_7"
      return 0x67
    Case "key_8"
      return 0x68
    Case "key_9"
      return 0x69
    Case "c"
      return 0x43
  EndSwitch
EndFunc

Func scheduleActions()
  $hwnd = WinGetHandle($windowName)

  Local $processedActions[10]
  Local $lastProcessedIndex = 0
  ;ConsoleWrite("Scheduling actions " & UBound($configurations)&@CRLF)
  For $i = 0 to UBound($configurations) -1
    Local $action = $configurations[$i]
    If Not $action[5] Then
      ContinueLoop
    EndIf

    Local $lastProcessed = 0
    Local $delta = TimerDiff($action[6])
    If $delta > ($action[3] * 1000) Then
      $action[6] = TimerInit()
      $configurations[$i] = $action
      $processedActions[$lastProcessedIndex] = $action
      $lastProcessedIndex = $lastProcessedIndex + 1
      ConsoleWrite("Process action: {combo: '" & $action[1] & "', delay: '" & $action[3] & "', active: '" & $action[5] & "', time: '" & $action[6] & "'}" & @CRLF)
      sendKeyDown(retrieveKeyCode($action[1]))
    EndIf
  Next

  ;Sleep($keyDownTime)
  For $i = 0 to $lastProcessedIndex - 1
    Local $action = $processedActions[$i]
    sendKeyUp(retrieveKeyCode($action[1]))
  Next
EndFunc

Func generateKeyLabelsForCombo()
  Local $allKeys = ""

  For $i = 1 To 10 Step +1
    $allKeys = $allKeys & "|" & $keyPrefix & $i
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
  Local $defaultKey = "key_" & $rowIndex
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

Func createGui()
  ; Create a GUI with various controls.
  Local $hGUI = GUICreate("FlyffBot", $MAX_WIDTH, $MAX_ROWS*($rowHeight+$padding))

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
  Local $windowNameInput = GUICtrlCreateInput($windowName, $xPos, $yPos, $width)
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
  $pipe1[1] = GUICtrlCreateLabel("Bot status: Paused - (Ctrl+Shift+1)", $xPos, $yPos, $width)
  $pipe1[2] = GUICtrlCreateLabel("Bot status: Running... - (Ctrl+Shift+1)", $xPos, $yPos, $width)
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
