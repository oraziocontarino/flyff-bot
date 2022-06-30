#include <WinAPISysWin.au3>
#include <WindowsConstants.au3>
#include <GUIConstantsEx.au3>

Global $maxConfigurations = 10
Global $configurations[$maxConfigurations]
Global $keyPrefix = "key_"
Global $keyDownTime = 1000
Global $windowName = "Flyff Universe – Mozilla Firefox"

Func sendKeyDown($hwnd, $keyCode)
	ConsoleWrite("Sending KEYDOWN " & $keyCode & @CRLF)
	_WinAPI_PostMessage($hwnd, $WM_KEYDOWN, $keyCode, 0)
EndFunc

Func sendKeyUp($hwnd, $keyCode)
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
	EndSwitch
EndFunc

Func scheduleActions()
	$hwnd = WinGetHandle($windowName)
	
	Dim $processedActions[10]
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
			sendKeyDown($hwnd, retrieveKeyCode($action[1]))
		EndIf
	Next
	
	;Sleep($keyDownTime)
	For $i = 0 to $lastProcessedIndex - 1
		Local $action = $processedActions[$i]
		sendKeyUp($hwnd, retrieveKeyCode($action[1]))
	Next
EndFunc

Func generateKeyLabelsForCombo()
	Local $allKeys = ""
	
	For $i = 0 To 10 Step +1
		$allKeys = $allKeys & "|" & $keyPrefix & $i
	Next
	return StringTrimLeft($allKeys, 1)
EndFunc

Func addBindingRow($rowIndex)
	Local $rowHeight = 30
	Local $padding = 16
	Local $paddingSm = 2
	Local $xPos = $padding;
	Local $yPost = (($rowIndex+1) *  $rowHeight) + $padding;
	Local $width = 0
	
	; Key list - Label
	$width=18
	Local $keyLabel = GUICtrlCreateLabel("Key:", $xPos, $yPost, $width)
	$xPos = $xPos + $width + $paddingSm
	
	; Key list - Combo
	$width=75
	Local $keys = generateKeyLabelsForCombo()
	Local $defaultKey = "key_" & $rowIndex
	Local $combo = GUICtrlCreateCombo($defaultKey, $xPos, $yPost, $width)
	GUICtrlSetData($combo, $keys, $defaultKey)
	$xPos = $xPos + $width + $padding
	
	; Delay - Label
	$width = 45
	Local $delayLabel = GUICtrlCreateLabel("Delay (s):", $xPos, $yPost, $width)
	$xPos = $xPos + $width + $paddingSm
	
	; Delay - Input
	Local $width = 25
	Local $delayInput = GUICtrlCreateInput("1", $xPos, $yPost, $width)
	$xPos = $xPos + $width + $padding
	
	; Enabled - Checkbox
	$width = 50
	Local $enabled = GUICtrlCreateCheckbox("Active", $xPos, $yPost, $width)
	$xPos = $xPos + $width + $padding
	
	; Prepare output
	Dim $out[7]
	$out[0] = $combo
	$out[1] = GUICtrlRead($combo)
    $out[2] = $delayInput
    $out[3] = GUICtrlRead($delayInput)
    $out[4] = $enabled
    $out[5] = ((GUICtrlRead($enabled)) == $GUI_CHECKED)
	$out[6] = TimerInit() ; Initial last time invoked value
	return $out
EndFunc

Func createGui()
	; Create a GUI with various controls.
	Local $hGUI = GUICreate("FlyffBot", 300, 350)
	
	Local $windowNameLabel = GUICtrlCreateLabel("Window Name:", 8, 8, 80)
	Local $windowNameInput = GUICtrlCreateInput($windowName, 88, 8, 200)
	
	For $i = 0 To ($maxConfigurations - 1) Step 1
		$configurations[$i] = addBindingRow($i)
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
				EndSwitch
				$configurations[$i] = $action
			Next
			$delta = TimerDiff($begin)
			If $delta > 500 Then
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