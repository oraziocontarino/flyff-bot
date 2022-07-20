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


Func Pipe_initPipe($pipeInitData)
  Local $pipe = HashMap_init(25)
  Local $rowHeight = HashMap_getFieldValue($pipeInitData, "rowHeight")
  Local $padding = HashMap_getFieldValue($pipeInitData, "padding")
  Local $maxWidth = HashMap_getFieldValue($pipeInitData, "maxWidth")
  Local $maxHeight = HashMap_getFieldValue($pipeInitData, "maxHeight")
  Local $offsetX = HashMap_getFieldValue($pipeInitData, "offsetX")

  local $maxConfigurations = HashMap_putFieldRaw($pipe, "maxConfigurations", 10)
  local $configurations[$maxConfigurations[1]]

  HashMap_putFieldRaw($pipe, "configurations", $configurations)

  HashMap_putFieldRaw($pipe, "keyDownTime", 1000)

  HashMap_putFieldRaw($pipe, "rowHeight", $rowHeight)

  HashMap_putFieldRaw($pipe, "padding", $padding)

  HashMap_putFieldRaw($pipe, "maxWidth", $maxWidth)

  HashMap_putFieldRaw($pipe, "maxHeight", $maxHeight)

  HashMap_putFieldRaw($pipe, "offsetX", $offsetX)

  HashMap_putFieldRaw($pipe, "paddingSm", 2)

  HashMap_putFieldRaw($pipe, "windowName", Null)

  HashMap_putFieldRaw($pipe, "labelIdle", Null)

  HashMap_putFieldRaw($pipe, "labelPaused", Null)

  HashMap_putFieldRaw($pipe, "labelRunning", Null)

  HashMap_putFieldRaw($pipe, "activeActionsCounter", 0)

  HashMap_putFieldRaw($pipe, "lastStatus", "IDLE_NO_ACTIONS")

  HashMap_putFieldRaw($pipe, "forcePaused", false)

  HashMap_putFieldRaw($pipe, "maxWidth", 200)

  HashMap_putFieldRaw($pipe, "windowNameInput", Null)

  HashMap_putFieldRaw($pipe, "windowNameRegex", ".*Flyff.*Universe.*")

  HashMap_putFieldRaw($pipe, "pipeSeparator", Null)

  HashMap_putFieldRaw($pipe, "hwnd", Null)

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
  ConsoleWrite("Update list: {new: '" & $windowComboItems[1] & "', old:'" & $oldComboItems & "'}" & @CRLF)
  HashMap_putFieldRaw($pipe, "windowComboItems", $windowComboItems[1])
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

Func addBindingRow(ByRef $pipe, $rowIndex, $originalXPos)
  Local $paddingSm = HashMap_getFieldValue($pipe, "paddingSm")
  Local $padding = HashMap_getFieldValue($pipe, "padding")

  Local $xPos = $originalXPos
  Local $yPos = 0
  Local $width = 0

  ; Increment row
  HashMap_incrementAndGetValue($pipe, "nextRowId", 1)
  ; Increment height
  $yPos = calculateNextRowYPos($pipe)
  ; Reset width
  $xPos = $originalXPos + 8

  ; Key list - Label
  $width = 18
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
  HashMap_putFieldRaw($pipe, "configurations", $configurations)
EndFunc

Func initGuiElements(ByRef $pipe)
  Local $offsetX = HashMap_getFieldValue($pipe, "offsetX")
  Local $originalXPos = $offsetX + 8
  Local $xPos = $originalXPos + 8
  Local $yPos = 8

  Local $paddingSm = HashMap_getFieldValue($pipe, "paddingSm")
  Local $padding = HashMap_getFieldValue($pipe, "padding")
  Local $windowName = HashMap_getFieldValue($pipe, "windowName")
  Local $windowNameRegex = HashMap_getFieldValue($pipe, "windowNameRegex")
  Local $maxWidth = HashMap_getFieldValue($pipe, "maxWidth")
  Local $maxConfigurations = HashMap_getFieldValue($pipe, "maxConfigurations")
  Local $nextRowId = HashMap_putFieldRaw($pipe, "nextRowId", 0)
  Local $width = 0

  ; Window Name - Label
  $width = 80
  Local $windowNameLabel = GUICtrlCreateLabel("Window Name:", $xPos, $yPos, $width)
  $xPos = $xPos + $width + $padding

  ; Window Name - Input
  $width = 200
  Local $windowComboItems = buildWindowListComboItems($windowNameRegex)
  Local $windowNameInput = GUICtrlCreateCombo($windowComboItems[0], $xPos, $yPos, $width)
  HashMap_putFieldRaw($pipe, "windowName", $windowComboItems[0])
  HashMap_putFieldRaw($pipe, "windowNameInput", $windowNameInput)
  HashMap_putFieldRaw($pipe, "windowComboItems", $windowComboItems[1])
  GUICtrlSetData($windowNameInput, $windowComboItems[1], $windowName)
  $xPos = $xPos + $width + $padding

  ; Increment row - increment height, reset width
  HashMap_incrementAndGetValue($pipe, "nextRowId", 1)
  $yPos = calculateNextRowYPos($pipe)
  $xPos = $originalXPos + 8

  ; Pipeline 1 Header - Label
  $width = $maxWidth - $padding
  Local $pipeStatus[3]
  $pipeStatus[0] = "IDLE_NO_ACTIONS"
  $pipeStatus[1] = "PAUSED"
  $pipeStatus[2] = "RUNNING"

  HashMap_putFieldRaw($pipe, "labelIdle", GUICtrlCreateLabel("Bot status: Idle - No active actions", $xPos, $yPos, $width))
  HashMap_putFieldRaw($pipe, "labelPaused", GUICtrlCreateLabel("Bot status: Paused - (Alt+Shift+x)", $xPos, $yPos, $width))
  HashMap_putFieldRaw($pipe, "labelRunning", GUICtrlCreateLabel("Bot status: Running... - (Alt+Shift+x)", $xPos, $yPos, $width))
  HashMap_putFieldRaw($pipe, "activeActionsCounter", 0)
  HashMap_putFieldRaw($pipe, "lastStatus", "IDLE_NO_ACTIONS")
  HashMap_putFieldRaw($pipe, "forcePaused", false)

  GUICtrlSetState(HashMap_getFieldValue($pipe, "labelPaused"), $GUI_HIDE)
  GUICtrlSetState(HashMap_getFieldValue($pipe, "labelRunning"), $GUI_HIDE)

  ; Increment row - increment height, reset width
  HashMap_incrementAndGetValue($pipe, "nextRowId", 1)
  $yPos = calculateNextRowYPos($pipe)
  $xPos = $originalXPos + 8

  ; HotKey 1 Info
  GUICtrlCreateLabel("Toggle Pause: Shift+Alt+X", $xPos, $yPos, $width)

  ; Increment row - increment height, reset width
  HashMap_incrementAndGetValue($pipe, "nextRowId", 1)
  $yPos = calculateNextRowYPos($pipe)
  $xPos = $originalXPos + 8

  ; HotKey 1 Info
  GUICtrlCreateLabel("Use ActionSlot: Shift+Alt+C", $xPos, $yPos, $width)

  For $i = 0 To ($maxConfigurations - 1) Step 1
    addBindingRow($pipe, $i, $originalXPos)
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
  Local $pipeSeparator = GUICtrlCreateGraphic($offsetX + 8 + $maxWidth, 8, 2, $maxHeight-8)
  GUICtrlSetBkColor(-1, 0x000000)
  HashMap_putFieldRaw($pipe, "pipeSeparator", $pipeSeparator)
EndFunc
