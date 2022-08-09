package flyffbot.services;

import flyffbot.dto.HotKeyDto;
import flyffbot.enums.EventEnum;
import flyffbot.enums.KeyStatus;
import flyffbot.gui.FBFrame;
import flyffbot.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.SwingDispatchService;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class KeyDownHookService implements NativeKeyListener {
	@Autowired
	private FBFrame fbFrame;

	@Autowired
	private UserConfigService userConfigService;

	@Value("${disable-key-down-hook}")
	private boolean isDisabled;



	private Map<Integer, KeyStatus> keyCodeStatusMap;
	private List<HotKeyDto> hotKeys;

	@PostConstruct
	public void init(){
		if(isDisabled){
			return;
		}

		try {
			keyCodeStatusMap = new HashMap<>();
			// Register hotkeys:
			hotKeys = new ArrayList<>(List.of(
					HotKeyDto.builder()
							.event(EventEnum.ADD_PIPE)
							.keys(Set.of(NativeKeyEvent.VC_ALT_L, NativeKeyEvent.VC_A))
							.build(),
					HotKeyDto.builder()
							.event(EventEnum.REMOVE_PIPE)
							.keys(Set.of(NativeKeyEvent.VC_ALT_L, NativeKeyEvent.VC_D))
							.build()
			));

			// Initialize keys UP status
			initializeKeyUpMap();

			GlobalScreen.setEventDispatcher(new SwingDispatchService());
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			log.error("There was a problem registering the native hook.", ex);
			System.exit(1);
		}

		GlobalScreen.addNativeKeyListener(this);

		fbFrame.initHooks(this);
	}

	private void initializeKeyUpMap() {
		keyCodeStatusMap.clear();

		hotKeys.stream()
				.map(HotKeyDto::getKeys)
				.reduce(new HashSet<>(), (a, b) -> {
					a.addAll(b);
					return a;
				})
				.forEach(key -> keyCodeStatusMap.put(key, KeyStatus.UP));
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		val keyCode = e.getKeyCode();
		if(!keyCodeStatusMap.containsKey(keyCode)){
			return;
		}
		keyCodeStatusMap.put(keyCode, KeyStatus.DOWN);
		handleEvent();
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		val keyCode = e.getKeyCode();
		if(!keyCodeStatusMap.containsKey(keyCode)){
			return;
		}
		keyCodeStatusMap.put(keyCode, KeyStatus.UP);
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent e) {	
	}

	public void addKeyBinding(String pipeId, int pipeIndex){
		hotKeys.add(
				HotKeyDto.builder()
						.event(EventEnum.TOGGLE_PAUSE)
						.pipeId(pipeId)
						.keys(Set.of(NativeKeyEvent.VC_SHIFT_L, NativeKeyEvent.VC_1 + (pipeIndex*3)))
						.build()
		);
		hotKeys.add(
				HotKeyDto.builder()
						.event(EventEnum.USE_ACTION_SLOT)
						.pipeId(pipeId)
						.keys(Set.of(NativeKeyEvent.VC_SHIFT_L, NativeKeyEvent.VC_2 + (pipeIndex*3)))
						.build()
		);
		hotKeys.add(
				HotKeyDto.builder()
						.event(EventEnum.USE_CUSTOM_ACTION_SLOT)
						.pipeId(pipeId)
						.keys(Set.of(NativeKeyEvent.VC_SHIFT_L, NativeKeyEvent.VC_3 + (pipeIndex*3)))
						.build()
		);

		initializeKeyUpMap();

		log.debug("addKeyBinding - {} - Registered TOGGLE_PAUSE: SHIFT + 1", pipeId);
		log.debug("addKeyBinding - {} - Registered USE_ACTION_SLOT: SHIFT + 2", pipeId);
		log.debug("addKeyBinding - {} - Registered USE_CUSTOM_ACTION_SLOT: SHIFT + 3", pipeId);
	}

	public void removeKeyBinding(String pipeId){
		hotKeys = hotKeys.stream()
				// Keep global hotkeys and other pipe hotkeys
				.filter(item -> !StringUtils.equals(item.getPipeId(), pipeId))
				.collect(Collectors.toList());
		initializeKeyUpMap();
	}
	private void handleEvent(){
		hotKeys.forEach(item -> {
			val match = item.getKeys()
					.stream()
					.allMatch(key -> keyCodeStatusMap.get(key) == KeyStatus.DOWN);
			if(!match){
				return;
			}
			log.debug("Running: {}", item.getEvent());
			switch (item.getEvent()){
				case ADD_PIPE:
					fbFrame.addPipe();
					break;
				case REMOVE_PIPE:
					fbFrame.removePipe();
					break;
				case TOGGLE_PAUSE:
					fbFrame.togglePause(item.getPipeId());
					break;
				case USE_ACTION_SLOT:
					fbFrame.useActionSlot(item.getPipeId(), List.of(Utils.toHexString(KeyEvent.VK_C)));
					break;
				case USE_CUSTOM_ACTION_SLOT:
					fbFrame.useCustomActionSlot(item.getPipeId());
					break;
				default:
					log.error("Unexpected action found: {}", item.getEvent());
			}
		});
	}
}
