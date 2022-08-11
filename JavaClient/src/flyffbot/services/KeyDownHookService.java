package flyffbot.services;

import flyffbot.dto.HotKeyDto;
import flyffbot.enums.EventEnum;
import flyffbot.enums.KeyStatus;
import flyffbot.gui.FBFrame;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
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
	private AtomicReference<List<HotKeyDto>> hotKeys;

	@PostConstruct
	public void init(){
		if(isDisabled){
			return;
		}

		try {
			keyCodeStatusMap = new HashMap<>();
			// Register hotkeys:
			hotKeys = new AtomicReference<>(new ArrayList<>(List.of(
					HotKeyDto.builder()
							.event(EventEnum.ADD_PIPE)
							.keys(Set.of(NativeKeyEvent.VC_ALT_L, NativeKeyEvent.VC_A))
							.build(),
					HotKeyDto.builder()
							.event(EventEnum.REMOVE_PIPE)
							.keys(Set.of(NativeKeyEvent.VC_ALT_L, NativeKeyEvent.VC_D))
							.build()
			)));

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

		hotKeys.getAndUpdate(list -> {
			val cpy = new ArrayList<>(list);
			cpy.stream()
					.map(HotKeyDto::getKeys)
					.reduce(new HashSet<>(), (a, b) -> {
						a.addAll(b);
						return a;
					})
					.forEach(key -> keyCodeStatusMap.put(key, KeyStatus.UP));
			return cpy;
		});
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
		hotKeys.getAndUpdate(list -> {
			val cpy = new ArrayList<>(list);
			cpy.add(HotKeyDto.builder()
					.event(EventEnum.TOGGLE_PAUSE)
					.pipeId(pipeId)
					.keys(Set.of(NativeKeyEvent.VC_SHIFT_L, NativeKeyEvent.VC_1 + (pipeIndex * 2)))
					.build()
			);
			cpy.add(HotKeyDto.builder()
					.event(EventEnum.USE_CUSTOM_ACTION_SLOT)
					.pipeId(pipeId)
					.keys(Set.of(NativeKeyEvent.VC_SHIFT_L, NativeKeyEvent.VC_2 + (pipeIndex * 2)))
					.build()
			);
			return cpy;
		});

		initializeKeyUpMap();

		log.debug("addKeyBinding - {} - Registered TOGGLE_PAUSE: SHIFT + {}", pipeId, (1+(pipeIndex*2)));
		log.debug("addKeyBinding - {} - Registered USE_CUSTOM_ACTION_SLOT: SHIFT + {}", pipeId, (2+(pipeIndex*2)));
	}

	public void removeKeyBinding(String pipeId){
		hotKeys.getAndUpdate(list -> {
			val cpy = new ArrayList<>(list);
			return cpy.stream()
					// Keep global hotkeys and other pipe hotkeys
					.filter(item -> !StringUtils.equals(item.getPipeId(), pipeId))
					.collect(Collectors.toList());
		});
		initializeKeyUpMap();
	}
	private void handleEvent(){
		hotKeys.getAndUpdate(list -> {
			val cpy = new ArrayList<>(list);
			cpy.forEach(item -> {
				val match = item.getKeys()
						.stream()
						.allMatch(key -> keyCodeStatusMap.get(key) == KeyStatus.DOWN);
				if (!match) {
					return;
				}
				log.debug("Running: {}", item.getEvent());
				switch (item.getEvent()) {
					case ADD_PIPE:
						fbFrame.addPipe();
						break;
					case REMOVE_PIPE:
						fbFrame.removePipe();
						break;
					case TOGGLE_PAUSE:
						fbFrame.togglePause(item.getPipeId());
						break;
					case USE_CUSTOM_ACTION_SLOT:
						fbFrame.useCustomActionSlot(item.getPipeId());
						break;
					default:
						log.error("Unexpected action found: {}", item.getEvent());
				}
			});
			return cpy;
		});
	}
}
