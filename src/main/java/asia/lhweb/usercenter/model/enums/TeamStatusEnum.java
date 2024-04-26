package asia.lhweb.usercenter.model.enums;

/**
 * 团队状态枚举类，定义了团队可能的状态及其相关信息。
 *
 * @author 罗汉
 * @date 2024/04/25
 */
public enum TeamStatusEnum {
    // 公开状态，允许任何人加入
    PUBLIC (0, "可加入"),
    // 私有状态，只有被邀请的人才能加入
    PRIVATE(1, "私有"),
    // 加密状态，团队信息经过加密，只有拥有密钥的人才能查看和加入
    JIAMI(2, "加密"),
    // 满员状态，团队已经达到了最大成员数量，不再接受新成员
    FULL(3, "满员");
    public static TeamStatusEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        if (value < 0 || value > 4) {
            return null;
        }
        for (TeamStatusEnum teamStatusEnum : values()) {
            if (teamStatusEnum.getValue() == value) {
                return teamStatusEnum;
            }
        }
        return null;
    }
    // 枚举值的内部状态代码
    private int value;
    // 枚举值对应的文字描述
    private String text;

    /**
     * 构造函数用于初始化枚举实例的状态值和文字描述。
     *
     * @param value 状态的内部数值表示。
     * @param text 状态的文字描述。
     */
    TeamStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 获取状态的内部数值表示。
     *
     * @return 状态的数值表示。
     */
    public int getValue() {
        return value;
    }

    /**
     * 获取状态的文字描述。
     *
     * @return 状态的文字描述。
     */
    public String getText() {
        return text;
    }
}
