Global $TYPE_NUMBER = "NUMBER"
Global $TYPE_STRING = "STRING"
Global $TYPE_BOOLEAN = "BOOLEAN"
Global $TYPE_MAP = "MAP"
Global $TYPE_ARRAY = "ARRAY"
Global $TYPE_GUI = "GUI"
Global $TYPE_HWND = "HWND"
Global $TYPE_BINDING_ROW = "BINDING_ROW"

Func Field_init($fieldName, $fieldValue, $type)
  Local $field[3] ;name - value - type
  $field[0] = $fieldName
  $field[1] = $fieldValue
  $field[2] = $type
  return $field
EndFunc

Func Field_getName(ByRef $field)
  if $field = Null Then
    println("- Field_getName over Null field. Cannot get name")
    return Null
  Else
    return $field[0]
  EndIf
EndFunc

Func Field_getValue(ByRef $field)
  if $field = Null Then
    println("- Field_getValue over Null field. Cannot get value")
    return Null
  Else
    return $field[1]
  EndIf
EndFunc

Func Field_getType(ByRef $field)
  if $field = Null Then
    println("- Field_getType over Null field. Cannot get type")
    return Null
  Else
    return $field[2]
  EndIf
EndFunc

Func Field_setValue(ByRef $field, $newValue, $type)
  if $field = Null Then
    println("- Field_setValue over Null field. Cannot set value=" & $newValue & ", type=" & $type)
    return
  EndIf
  $field[1] = $newValue
  $field[2] = $type
EndFunc

Func Field_toString(ByRef $field)
  If $field = Null Then
    return "null_field"
  Else
    return "{name: '"& $field[0] &"', value: '"& $field[1] &"', type: '"& $field[2] &"'}"
  EndIf
EndFunc
