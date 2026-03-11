package basicmod.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ThornsPower;
import basicmod.BasicMod;
import basicmod.util.TextureLoader;

public class ShenZhuStatuePower extends AbstractPower {
    public static final String POWER_ID = BasicMod.makeID("ShenZhuStatuePower");
    // 我们暂时硬编码中文字符串或者使用预留的资源
    public static final String NAME = "圣主石像";
    public static final String[] DESCRIPTIONS = { "你是一尊石像。你的 #y攻击牌 费用加倍。获得的 #y护甲 翻三倍。" };

    public ShenZhuStatuePower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        this.type = PowerType.DEBUFF; // 标记为一个负面/特殊机制状态

        // Load region from an existing texture, or fallback to artifact
        Texture normalTexture = TextureLoader.getTexture(BasicMod.powerPath("statue_state_32.png"));
        Texture hiDefImage = TextureLoader.getTexture(BasicMod.powerPath("statue_state_84.png"));
        if (normalTexture != null && hiDefImage != null) {
            this.region128 = new TextureAtlas.AtlasRegion(hiDefImage, 0, 0, hiDefImage.getWidth(),
                    hiDefImage.getHeight());
            this.region48 = new TextureAtlas.AtlasRegion(normalTexture, 0, 0, normalTexture.getWidth(),
                    normalTexture.getHeight());
        } else {
            this.loadRegion("malleable"); // 找不到图片时的兜底图
        }

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void onInitialApplication() {
        // 石像状态附加时，获得3层荆棘
        addToTop(new ApplyPowerAction(this.owner, this.owner, new ThornsPower(this.owner, 3), 3));
    }

    @Override
    public void onRemove() {
        // 移除石像状态时，削减这3层荆棘
        if (this.owner.hasPower(ThornsPower.POWER_ID)) {
            addToTop(new ApplyPowerAction(this.owner, this.owner, new ThornsPower(this.owner, -3), -3));
        }
    }
}
