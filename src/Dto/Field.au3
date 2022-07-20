#include <WinAPISysWin.au3>
#include <WindowsConstants.au3>
#include <GUIConstantsEx.au3>

Func Field_init($fieldName, $fieldValue)
  Local $field[2] ;name - value
  $field[0] = $fieldName
  $field[1] = $fieldValue
  return $field
EndFunc

Func Field_getName(ByRef $field)
  return $field[0]
EndFunc

Func Field_getValue(ByRef $field)
  return $field[1]
EndFunc

Func Field_setValue(ByRef $field, $newValue)
  $field[1] = $newValue
EndFunc

; Local $field = Field_init("test", 123)
; Local $name = Field_getName($field)
; Local $value = Field_getValue($field)
; ConsoleWrite("@@@ Field name: " & $name & @CRLF)
; ConsoleWrite("@@@ Field value: " & $value & @CRLF)
