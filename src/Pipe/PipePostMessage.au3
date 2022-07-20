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


Func PipePostMessage_retrieveKeyCode($rawKey)
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

Func PipePostMessage_sendKeyDown(ByRef $pipe, $keyData)
  Local $hwnd = HashMap_getFieldValue($pipe, "hwnd")
  Local $windowName = HashMap_getFieldValue($pipe, "windowName")
  For $i = 1 To $keyData[0] Step 1
    Local $keyCode = $keyData[$i]
    ;ConsoleWrite("Sending KEYDOWN " & $keyCode & @CRLF)
    _WinAPI_PostMessage($hwnd, $WM_KEYDOWN, $keyCode, 0)
  Next
EndFunc

Func PipePostMessage_sendKeyUp(ByRef $pipe, $keyData)
  Local $hwnd = HashMap_getFieldValue($pipe, "hwnd")
  For $i = 1 To $keyData[0] Step 1
    Local $keyCode = $keyData[$i]
    ;ConsoleWrite("Sending KEYUP " & $keyCode & @CRLF)
    _WinAPI_PostMessage($hwnd, $WM_KEYUP, $keyCode, 0)
  Next
EndFunc


