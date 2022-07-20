Func PipeEvent_onWindowNameChange(ByRef $pipe, $guiMessage)
  Local $windowNameInput = HashMap_getFieldValue($pipe, "windowNameInput")
  if $guiMessage = $windowNameInput Then
    Local $windowName = GUICtrlRead($windowNameInput)
    HashMap_putFieldRaw($pipe, "windowName", $windowName)
    ConsoleWrite("Updated window name to: " & $windowName & @CRLF)
  EndIf
EndFunc

Func PipeEvent_onConfigurationChange(ByRef $pipe, $guiMessage)
  Local $configurations = HashMap_getFieldValue($pipe, "configurations")
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
          HashMap_incrementAndGetValue($pipe, "activeActionsCounter", 1)
        Else
          HashMap_incrementAndGetValue($pipe, "activeActionsCounter", -1)
        EndIf
    EndSwitch
    $configurations[$i] = $action
  Next
  HashMap_putFieldRaw($pipe, "configurations", $configurations)
EndFunc

Func PipeEvent_onStatusChange(ByRef $pipe, $guiMessage)
  Local $pipeStatus[3]
  $pipeStatus[0] = "IDLE_NO_ACTIONS"
  $pipeStatus[1] = "PAUSED"
  $pipeStatus[2] = "RUNNING"

  Local $labelIdle = HashMap_getFieldValue($pipe, "labelIdle")
  Local $labelPaused = HashMap_getFieldValue($pipe, "labelPaused")
  Local $labelRunning = HashMap_getFieldValue($pipe, "labelRunning")
  Local $activeActionsCounter = HashMap_getFieldValue($pipe, "activeActionsCounter")
  Local $lastStatus = HashMap_getFieldValue($pipe, "lastStatus")
  Local $isPaused = HashMap_getFieldValue($pipe, "forcePaused")

  Local $hasActionEnabled = $activeActionsCounter > 0
  Switch($lastStatus) ; Read current pipe state
    Case $pipeStatus[0] ; Current status: IDLE
      If $hasActionEnabled And (Not $isPaused) Then
        GUICtrlSetState($labelIdle, $GUI_HIDE) ; Hide Idle label
        GUICtrlSetState($labelPaused, $GUI_HIDE) ; Hide Paused label
        GUICtrlSetState($labelRunning, $GUI_SHOW) ; Show Running label
        HashMap_putFieldRaw($pipe, "lastStatus", $pipeStatus[2]) ; Update pipe state to: RUNNING
      ElseIf $hasActionEnabled Then
        GUICtrlSetState($labelIdle, $GUI_HIDE) ; Hide Idle label
        GUICtrlSetState($labelPaused, $GUI_SHOW) ; Show Paused label
        GUICtrlSetState($labelRunning, $GUI_HIDE) ; Hide Running label
        HashMap_putFieldRaw($pipe, "lastStatus", $pipeStatus[1]) ; Update pipe state to: PAUSED
      EndIf
    Case $pipeStatus[1] ; Current status: PAUSED
      If $hasActionEnabled And (Not $isPaused) Then
        GUICtrlSetState($labelIdle, $GUI_HIDE) ; Hide Idle label
        GUICtrlSetState($labelPaused, $GUI_HIDE) ; Hide Paused label
        GUICtrlSetState($labelRunning, $GUI_SHOW) ; Show Running label
        HashMap_putFieldRaw($pipe, "lastStatus", $pipeStatus[2]) ; Update pipe state to: RUNNING
      ElseIf Not $hasActionEnabled And (Not $isPaused) Then
        GUICtrlSetState($labelIdle, $GUI_SHOW) ; Show Idle label
        GUICtrlSetState($labelPaused, $GUI_HIDE) ; Hide Paused label
        GUICtrlSetState($labelRunning, $GUI_HIDE) ; Hide Running label
        HashMap_putFieldRaw($pipe, "lastStatus", $pipeStatus[0]) ; Update pipe state to: IDLE
      EndIf
    Case $pipeStatus[2] ; Current status: RUNNING
      If $hasActionEnabled And $isPaused Then
        GUICtrlSetState($labelIdle, $GUI_HIDE) ; Hide Idle label
        GUICtrlSetState($labelPaused, $GUI_SHOW) ; Show Paused label
        GUICtrlSetState($labelRunning, $GUI_HIDE) ; Hide Running label
        HashMap_putFieldRaw($pipe, "lastStatus", $pipeStatus[1]) ; Update pipe state to: PAUSE
      ElseIf Not $hasActionEnabled And (Not $isPaused) Then
        GUICtrlSetState($labelIdle, $GUI_SHOW) ; Show Idle label
        GUICtrlSetState($labelPaused, $GUI_HIDE) ; Hide Paused label
        GUICtrlSetState($labelRunning, $GUI_HIDE) ; Hide Running label
        HashMap_putFieldRaw($pipe, "lastStatus", $pipeStatus[0]) ; Update pipe state to: IDLE
      EndIf
  EndSwitch
EndFunc




Func PipeEvent_onActionSchedule(ByRef $pipe)
  Local $isPaused = HashMap_getFieldValue($pipe, "forcePaused")
  If $isPaused Then
    return
  EndIf
  Local $windowName = HashMap_getFieldValue($pipe, "windowName")
  Local $configurations = HashMap_getFieldValue($pipe, "configurations")

  Local $hwnd = WinGetHandle($windowName)
  HashMap_putFieldRaw($pipe, "hwnd", $hwnd)

  Local $configurations = HashMap_getFieldValue($pipe, "configurations")

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
      PipePostMessage_sendKeyDown($pipe, PipePostMessage_retrieveKeyCode($action[1]))
      PipePostMessage_sendKeyUp($pipe, PipePostMessage_retrieveKeyCode($action[1]))
    EndIf
  Next
  HashMap_putFieldRaw($pipe, "configurations", $configurations)
EndFunc

Func PipeEvent_onTogglePause(ByRef $pipe)
  Local $isPaused = HashMap_getFieldValue($pipe, "forcePaused")
  Local $updatedIsPausedFlag = Not $isPaused ; Toggle Pause pipe
  HashMap_putFieldRaw($pipe, "forcePaused", $updatedIsPausedFlag)
EndFunc

Func PipeEvent_onActionSlot(ByRef $pipe)
  PipePostMessage_sendKeyDown($pipe, PipePostMessage_retrieveKeyCode("c"))
  PipePostMessage_sendKeyUp($pipe, PipePostMessage_retrieveKeyCode("c"))
EndFunc

Func PipeEvent_onRefreshWindowList(ByRef $pipe)
  Pipe_refreshWindowList($pipe)
EndFunc
