;#include "./Field.au3"

Func HashMap_init($size)
  Local $map[$size]
  For $i = 0 To $size - 1 Step 1
    $map[$i] = Null
  Next
  return $map
EndFunc

Func HashMap_putField(ByRef $map, $field)
  Local $size = Ubound($map)

  For $i = 0 To $size - 1 Step 1
    Local $currentField = $map[$i]
    If $currentField = Null Then
      $map[$i] = $field ; Insert new field
      return $map[$i]
    EndIf

    If Field_getName($currentField) = Field_getName($field) Then
      Field_setValue($map[$i], Field_getValue($field), Field_getType($field))
      return $map[$i]
    EndIf
  Next
EndFunc

Func HashMap_putFieldRaw(ByRef $map, $fieldName, $fieldValue, $fieldType)
  Local $field = Field_init($fieldName, $fieldValue, $fieldType)
  return HashMap_putField($map, $field)
EndFunc

Func HashMap_incrementAndGetValue(ByRef $map, $fieldName, $incValue)
  Local $fieldValue = HashMap_getFieldValue($map, $fieldName)
  Local $fieldType = HashMap_getFieldValue($map, $fieldName)
  $fieldValue = $fieldValue + $incValue
  Local $updatedField = HashMap_putFieldRaw($map, $fieldName, $fieldValue, $fieldType)
  return Field_getValue($updatedField)
EndFunc

Func HashMap_getField(ByRef $map, $fieldName)
  Local $size = Ubound($map)

  For $i = 0 To $size - 1 Step 1
    Local $currentField = $map[$i]
    If $currentField = Null Then
      return Null
    EndIf

    If Field_getName($currentField) = $fieldName Then
      return $currentField
    EndIf
  Next
EndFunc

Func HashMap_getFieldValue(ByRef $map, $fieldName)
  Local $field = HashMap_getField($map, $fieldName)
  If $field = Null Then
    println("- Field not found with given key: " & $fieldName)
  EndIf
  return Field_getValue($field)
EndFunc

Func HashMap_getFieldType(ByRef $map, $fieldName)
  Local $field = HashMap_getField($map, $fieldName)
  If $field = Null Then
    println("- Field not found with given key: " & $fieldName)
  EndIf
  return Field_getType($field)
EndFunc
