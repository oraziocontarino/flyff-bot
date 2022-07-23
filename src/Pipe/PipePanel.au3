;#include <WinAPISysWin.au3>
;#include <WindowsConstants.au3>
;#include <GUIConstantsEx.au3>
;#include "../Dto/HashMap.au3"

; START - SPOSTARE IN COMMONG
Global $DELIMITER = "►"
; END - SPOSTARE IN COMMONG

Local $keyPrefix = "Key: "
Local $keyAltPrefix = "Alt + "
Local $keyCtrlPrefix = "Ctrl + "


Func Pipe_initPipe($pipeInitData, $i)
  Local $pipe = HashMap_init(50)
  Local $rowHeight = HashMap_getFieldValue($pipeInitData, "rowHeight")
  Local $padding = HashMap_getFieldValue($pipeInitData, "padding")
  Local $maxWidth = HashMap_getFieldValue($pipeInitData, "maxWidth")
  Local $maxHeight = HashMap_getFieldValue($pipeInitData, "maxHeight")
  Local $offsetX = HashMap_getFieldValue($pipeInitData, "offsetX")

  local $maxConfigurations = HashMap_putFieldRaw($pipe, "maxConfigurations", 10, $TYPE_NUMBER)
  local $configurations[$maxConfigurations[1]]

  HashMap_putFieldRaw($pipe, "id", $i, $TYPE_NUMBER)

  HashMap_putFieldRaw($pipe, "configurations", $configurations, $TYPE_MAP)

  HashMap_putFieldRaw($pipe, "keyDownTime", 1000, $TYPE_NUMBER)

  HashMap_putFieldRaw($pipe, "rowHeight", $rowHeight, $TYPE_NUMBER)

  HashMap_putFieldRaw($pipe, "padding", $padding, $TYPE_NUMBER)

  HashMap_putFieldRaw($pipe, "maxWidth", $maxWidth, $TYPE_NUMBER)

  HashMap_putFieldRaw($pipe, "maxHeight", $maxHeight, $TYPE_NUMBER)

  HashMap_putFieldRaw($pipe, "offsetX", $offsetX, $TYPE_NUMBER)

  HashMap_putFieldRaw($pipe, "paddingSm", 2, $TYPE_NUMBER)

  HashMap_putFieldRaw($pipe, "windowName", Null, $TYPE_STRING)

  HashMap_putFieldRaw($pipe, "labelIdle", Null, $TYPE_GUI)

  HashMap_putFieldRaw($pipe, "labelPaused", Null, $TYPE_GUI)

  HashMap_putFieldRaw($pipe, "labelRunning", Null, $TYPE_GUI)

  HashMap_putFieldRaw($pipe, "activeActionsCounter", 0, $TYPE_NUMBER)

  HashMap_putFieldRaw($pipe, "lastStatus", "IDLE_NO_ACTIONS", $TYPE_STRING)

  HashMap_putFieldRaw($pipe, "forcePaused", false, $TYPE_BOOLEAN)

  HashMap_putFieldRaw($pipe, "windowNameInput", Null, $TYPE_GUI)

  HashMap_putFieldRaw($pipe, "windowNameRegex", ".*Flyff.*", $TYPE_STRING)

  HashMap_putFieldRaw($pipe, "pipeSeparator", Null, $TYPE_GUI)

  HashMap_putFieldRaw($pipe, "removeEvent", false, $TYPE_BOOLEAN)

  HashMap_putFieldRaw($pipe, "hwnd", Null, $TYPE_HWND)

  initGuiElements($pipe)

  return $pipe
EndFunc

Func Pipe_refreshWindowList(ByRef $pipe)
  Local $windowName = HashMap_getFieldValue($pipe, "windowName")
  Local $windowNameRegex = HashMap_getFieldValue($pipe, "windowNameRegex")
  Local $windowNameInput = HashMap_getFieldValue($pipe, "windowNameInput")

  Local $oldComboItems = HashMap_getFieldValue($pipe, "windowComboItems")
  Local $windowComboItems = buildWindowListComboItems($windowNameRegex)
  If $windowComboItems[1] = $oldComboItems Then
    return ; Nothing changed, do not update combobox
  EndIf
  println("Update list: {new: '" & $windowComboItems[1] & "', old:'" & $oldComboItems & "'}")
  HashMap_putFieldRaw($pipe, "windowComboItems", $windowComboItems[1], $TYPE_STRING)
  GUICtrlSetData($windowNameInput, $windowComboItems[1], $windowName)
EndFunc

Func buildWindowListComboItems($windowNameRegex)
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

  return $out
EndFunc

Func calculateNextRowYPos(ByRef $pipe)
  Local $padding = HashMap_getFieldValue($pipe, "padding")
  Local $rowHeight = HashMap_getFieldValue($pipe, "rowHeight")
  Local $nextRowId = HashMap_getFieldValue($pipe, "nextRowId")
  return ($nextRowId * ($rowHeight + $padding))
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

Func updateXPosNextRow(ByRef $pipe, ByRef $xPos, ByRef $yPos, $offsetX)
  HashMap_incrementAndGetValue($pipe, "nextRowId", 1)
  $xPos = $offsetX
  $yPos = calculateNextRowYPos($pipe)
EndFunc

Func addBindingRow(ByRef $pipe, $rowIndex, $offsetX)
  Local $bindingRow = HashMap_init(5)

  Local $paddingSm = HashMap_getFieldValue($pipe, "paddingSm")
  Local $padding = HashMap_getFieldValue($pipe, "padding")

  Local $xPos = $offsetX
  Local $yPos = 0
  Local $width = 0

  ; Increment row
  updateXPosNextRow($pipe, $xPos, $yPos, $offsetX)

  ; Key list - Label
  $width = 18
  Local $keyLabel = Gui_createLabel($bindingRow, "bindingRow_keyLabel_" & $rowIndex, "Key:", $xPos, $yPos, $width)
  $xPos = $xPos + $width + $paddingSm

  ; Key list - Combo
  $width=75
  Local $keys = generateKeyLabelsForCombo()
  Local $defaultKey = $keyPrefix & $rowIndex
  Local $combo = Gui_createCombo($bindingRow, "bindingRow_combo_" & $rowIndex, $defaultKey, $xPos, $yPos, $width, $keys)
  $xPos = $xPos + $width + $padding

  ; Delay - Label
  $width = 45
  Local $delayLabel = Gui_createLabel($bindingRow, "bindingRow_delayLabel_" & $rowIndex, "Delay (s):", $xPos, $yPos, $width)
  $xPos = $xPos + $width + $paddingSm

  ; Delay - Input
  Local $width = 25
  Local $delayInput = Gui_createInput($bindingRow, "bindingRow_delayInput_" & $rowIndex, "1", $xPos, $yPos, $width)
  $xPos = $xPos + $width + $padding

  ; Enabled - Checkbox
  $width = 50
  Local $enabled = Gui_createCheckbox($bindingRow, "bindingRow_enabledInput_" & $rowIndex, "Active", $xPos, $yPos, $width)
  $xPos = $xPos + $width + $padding

  ; Prepare new configuration item
  Local $out[7]
  $out[0] = $combo
  $out[1] = GUICtrlRead($combo)
  $out[2] = $delayInput
  $out[3] = GUICtrlRead($delayInput)
  $out[4] = $enabled
  $out[5] = ((GUICtrlRead($enabled)) == $GUI_CHECKED)
  $out[6] = TimerInit() ; Initial last time invoked value

  Local $configurations = HashMap_getFieldValue($pipe, "configurations")
  $configurations[$rowIndex] = $out
  HashMap_putFieldRaw($pipe, "configurations", $configurations, $TYPE_ARRAY)
  HashMap_putFieldRaw($pipe, "bindingRow_" & $rowIndex, $bindingRow, $TYPE_BINDING_ROW)
EndFunc

Func initGuiElements(ByRef $pipe)
  Local $offsetX = HashMap_getFieldValue($pipe, "offsetX")
  Local $xPos = $offsetX
  Local $yPos = 8

  Local $paddingSm = HashMap_getFieldValue($pipe, "paddingSm")
  Local $padding = HashMap_getFieldValue($pipe, "padding")
  Local $windowName = HashMap_getFieldValue($pipe, "windowName")
  Local $windowNameRegex = HashMap_getFieldValue($pipe, "windowNameRegex")
  Local $maxWidth = HashMap_getFieldValue($pipe, "maxWidth")
  Local $maxConfigurations = HashMap_getFieldValue($pipe, "maxConfigurations")
  Local $nextRowId = HashMap_putFieldRaw($pipe, "nextRowId", 0, $TYPE_NUMBER)
  Local $width = 0

  ; Window Name - Label
  $width = 80
  Gui_createLabel($pipe, "windowNameLabel", "Window Name:", $xPos, $yPos, $width)
  $xPos = $xPos + $width + $padding

  ; Window Name - Input
  $width = 200
  Local $windowComboItems = buildWindowListComboItems($windowNameRegex)
  Local $defaultKey = $windowComboItems[0]
  Local $keys = $windowComboItems[1]
  Gui_createCombo($pipe, "windowNameInput", $defaultKey, $xPos, $yPos, $width, $keys)
  HashMap_putFieldRaw($pipe, "windowName", $defaultKey, $TYPE_STRING)
  HashMap_putFieldRaw($pipe, "windowComboItems", $keys, $TYPE_STRING)
  $xPos = $xPos + $width + $padding

  ; Increment row - increment height, reset width
  updateXPosNextRow($pipe, $xPos, $yPos, $offsetX)

  ; Pipeline 1 Header - Label
  $width = $maxWidth - 16
  Local $pipeStatus[3]
  $pipeStatus[0] = "IDLE_NO_ACTIONS"
  $pipeStatus[1] = "PAUSED"
  $pipeStatus[2] = "RUNNING"

  Gui_createLabel($pipe, "labelIdle", "Bot status: Idle - No active actions", $xPos, $yPos, $width)
  Gui_createLabel($pipe, "labelPaused", "Bot status: Paused", $xPos, $yPos, $width)
  Gui_createLabel($pipe, "labelRunning", "Bot status: Running...", $xPos, $yPos, $width)
  HashMap_putFieldRaw($pipe, "activeActionsCounter", 0, $TYPE_NUMBER)
  HashMap_putFieldRaw($pipe, "lastStatus", "IDLE_NO_ACTIONS", $TYPE_STRING)
  HashMap_putFieldRaw($pipe, "forcePaused", false, $TYPE_BOOLEAN)

  GUICtrlSetState(HashMap_getFieldValue($pipe, "labelPaused"), $GUI_HIDE)
  GUICtrlSetState(HashMap_getFieldValue($pipe, "labelRunning"), $GUI_HIDE)

  ; Increment row - increment height, reset width
  updateXPosNextRow($pipe, $xPos, $yPos, $offsetX)


  ; HotKeys Info - Part 1
  Gui_createLabel($pipe, "togglePauseTip", "Toggle Pause: Shift + Alt + X", $xPos, $yPos, $width*0.5)
  Gui_createGraphic($pipe, "hotkeyInfoSeparator", $xPos + ($width*0.5)+8, $yPos-2, 2, 50, 0x000000)
  Gui_createLabel($pipe, "addPipe", "Add Pipe: Alt + A", $xPos + ($width*0.5)+16, $yPos, 120)

  ; Increment row - increment height, reset width
  updateXPosNextRow($pipe, $xPos, $yPos, $offsetX)

  ; HotKeys Info - Part 2
  Gui_createLabel($pipe, "toggleActionSlotTip", "Use ActionSlot: Shift + Alt + C", $xPos, $yPos, $width*0.5)
  Gui_createLabel($pipe, "removePipe", "Remove Pipe: Alt + D", $xPos + ($width*0.5)+16, $yPos, 120)

  For $i = 0 To ($maxConfigurations - 1) Step 1
    addBindingRow($pipe, $i, $offsetX)
  Next
EndFunc


Func Pipe_addPipeSeparator(ByRef $pipe)
  ; Pipe separator - Create vertical black line

  Local $pipeSeparator = HashMap_getFieldValue($pipe, "pipeSeparator")
  If $pipeSeparator <> Null Then
    return
  EndIf

  Local $maxWidth = HashMap_getFieldValue($pipe, "maxWidth")
  Local $maxHeight = HashMap_getFieldValue($pipe, "maxHeight")
  Local $offsetX = HashMap_getFieldValue($pipe, "offsetX")
  Local $x = $maxWidth + $offsetX -8
  Local $y = 8
  Local $width = 2
  Local $height = $maxHeight-8
  Gui_createGraphic($pipe, "pipeSeparator", $x, $y, $width, $height, 0x000000)
EndFunc

Func Pipe_removePipeSeparator(ByRef $pipe)
  GUICtrlDelete(HashMap_getFieldValue($pipe, "pipeSeparator"))
EndFunc

Func Pipe_remove(ByRef $pipe)
  For $i = 0 To UBound($pipe) - 1 Step 1
    Local $field = $pipe[$i]

    If $field = Null Then
      ContinueLoop
    EndIf

    Local $type = Field_getType($pipe[$i])
    Local $name = Field_getName($pipe[$i])
    Local $value = Field_getValue($pipe[$i])

    If $type = $TYPE_GUI Then
      ;println("- Remove GUI element:" & Field_toString($field))
      GUICtrlDelete($value)
      HashMap_putFieldRaw($pipe, $name, Null, $type)
    ElseIf $type = $TYPE_BINDING_ROW Then
      ;println("- Remove BINDING_ROW element:" & Field_toString($field))
      For $j = 0 To UBound($value) - 1 Step 1
        If Field_getType($value[$j]) = $TYPE_GUI Then
          ;println("--- Remove BINDING_ROW_SUB element:" & Field_toString($value[$j]))
          GUICtrlDelete(Field_getValue($value[$j]))
        EndIF
      Next
    ElseIf $type = $TYPE_HWND Then
      ;println("- Remove HWND element:" & Field_toString($field))
      HashMap_putFieldRaw($pipe, $name, Null, $type)
    ElseIf $name = "configurations" Then
      ;println("- Remove binding rows:" & Field_toString($field))
      GUICtrlDelete($value[0])
      GUICtrlDelete($value[2])
      GUICtrlDelete($value[4])
      Local $maxConfigurations = HashMap_getFieldValue($pipe, "maxConfigurations")
      Local $emptyConfig[$maxConfigurations]
      HashMap_putFieldRaw($pipe, $name, $emptyConfig, $TYPE_ARRAY)
    Else
      ;println("Scanning element:" & Field_toString($field))
    EndIf
  Next
EndFunc
