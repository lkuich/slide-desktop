package enums;

import java.util.HashMap;
import java.util.Map;

import davinci.Const;

public enum DeviceMessageType
{
    CLOSE(Const.DEVICE_MESSAGE_ID_BASE() + 2),
    FINGER_DOWN(Const.DEVICE_MESSAGE_ID_BASE() + 3),
    FINGER_TAP(Const.DEVICE_MESSAGE_ID_BASE() + 4),
    DOUBLE_DOWN(Const.DEVICE_MESSAGE_ID_BASE() + 5),
    DOUBLE_UP(Const.DEVICE_MESSAGE_ID_BASE() + 6),
    LONG_HOLD(Const.DEVICE_MESSAGE_ID_BASE() + 7),
    DOUBLE_TAP(Const.DEVICE_MESSAGE_ID_BASE() + 8),
    KEYBOARD(Const.DEVICE_MESSAGE_ID_BASE() + 9),
    ABSOLUTE(Const.DEVICE_MESSAGE_ID_BASE() + 10),
    RELATIVE(Const.DEVICE_MESSAGE_ID_BASE() + 11),
    ZOOM_IN(Const.DEVICE_MESSAGE_ID_BASE() + 12),
    ZOOM_OUT(Const.DEVICE_MESSAGE_ID_BASE() + 13),
    SCROLL_DOWN(Const.DEVICE_MESSAGE_ID_BASE() + 14),
    SCROLL_UP(Const.DEVICE_MESSAGE_ID_BASE() + 15),
    SCROLL_LEFT(Const.DEVICE_MESSAGE_ID_BASE() + 16),
    SCROLL_RIGHT(Const.DEVICE_MESSAGE_ID_BASE() + 17),
    CUT(Const.DEVICE_MESSAGE_ID_BASE() + 18),
    COPY(Const.DEVICE_MESSAGE_ID_BASE() + 19),
    PASTE(Const.DEVICE_MESSAGE_ID_BASE() + 20),
    SCREEN_SHOT(Const.DEVICE_MESSAGE_ID_BASE() + 21),
    DOUBLE_DOWN_PEN(Const.DEVICE_MESSAGE_ID_BASE() + 22),
    MOVE_CURSOR(0);

    private int id;

    private static Map<Integer, DeviceMessageType> messageTypeById;

    private DeviceMessageType(int messageId)
    {
        this.id = messageId;
    }


    public int toId()
    {
        return this.id;
    }

    private static void indexMessageTypeById()
    {
        messageTypeById = new HashMap<>();
        for (DeviceMessageType messageType : values())
        {
            messageTypeById.put(messageType.toId(), messageType);
        }
    }

    public static DeviceMessageType fromId(final int code)
    {
        if (code <= Const.DEVICE_MESSAGE_ID_BASE())
        {
            return MOVE_CURSOR;
        }

        if (messageTypeById == null)
        {
            indexMessageTypeById();
        }

        return messageTypeById.get(code);
    }
}