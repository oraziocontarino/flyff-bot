Func Gui_createLabel(ByRef $map, $mapKey, $label, $x, $y, $width)
  Local $guiElement = GUICtrlCreateLabel($label, $x, $y, $width)
  HashMap_putFieldRaw($map, $mapKey, $guiElement, $TYPE_GUI)

  GUICtrlSetResizing($guiElement, $GUI_DOCKALL)
  return $guiElement
EndFunc

Func Gui_createCombo(ByRef $map, $mapKey, $defaultKey, $x, $y, $width, $keys)
  Local $guiElement = GUICtrlCreateCombo($defaultKey, $x, $y, $width)
  GUICtrlSetData($guiElement, $keys, $defaultKey)
  HashMap_putFieldRaw($map, $mapKey, $guiElement, $TYPE_GUI)

  GUICtrlSetResizing($guiElement, $GUI_DOCKALL)
  return $guiElement
EndFunc

Func Gui_createInput(ByRef $map, $mapKey, $label, $x, $y, $width)
  Local $guiElement = GUICtrlCreateInput($label, $x, $y, $width)
  HashMap_putFieldRaw($map, $mapKey, $guiElement, $TYPE_GUI)

  GUICtrlSetResizing($guiElement, $GUI_DOCKALL)
  return $guiElement
EndFunc

Func Gui_createCheckbox(ByRef $map, $mapKey, $label, $x, $y, $width)
  Local $guiElement = GUICtrlCreateCheckbox($label, $x, $y, $width)
  HashMap_putFieldRaw($map, $mapKey, $guiElement, $TYPE_GUI)

  GUICtrlSetResizing($guiElement, $GUI_DOCKALL)
  return $guiElement
EndFunc

Func Gui_createGraphic(ByRef $map, $mapKey, $label, $x, $y, $width, $color)
  Local $guiElement = GUICtrlCreateGraphic($label, $x, $y, $width)
  GUICtrlSetBkColor($guiElement, $color)
  HashMap_putFieldRaw($map, $mapKey, $guiElement, $TYPE_GUI)

  GUICtrlSetResizing($guiElement, $GUI_DOCKALL)
  return $guiElement
EndFunc
