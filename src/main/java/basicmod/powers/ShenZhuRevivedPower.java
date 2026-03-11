package basicmod.powers;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class ShenZhuRevivedPower extends AbstractPower {
    public static final String POWER_ID = BasicMod.makeID("ShenZhuRevivedPower");
    public static final String NAME = "圣主复苏";
    public static final String[] DESCRIPTIONS = { "拥有鼠符咒：你恢复了真身。每回合开始时，额外获得 #b1 点 #y力量 。" };

    private int strGained = 1;

    public ShenZhuRevivedPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        this.type = PowerType.BUFF;
        this.loadRegion("demonForm"); // 使用原版的恶魔形态红眼图片作为标志
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void onInitialApplication() {
        // 第一回合刚附带时加1力量
        addToTop(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, 1), 1));
    }

    @Override
    public void atStartOfTurnPostDraw() {
        this.flash();
        addToBot(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, 1), 1));
        this.strGained += 1;
    }

    @Override
    public void onRemove() {
        // 移除时扣掉通过该被动获取的所有力量，避免失去该状态时力量残留
        if (this.owner.hasPower(StrengthPower.POWER_ID)) {
            addToTop(new com.megacrit.cardcrawl.actions.common.ReducePowerAction(this.owner, this.owner, StrengthPower.POWER_ID, this.strGained));
        }
    }
}
