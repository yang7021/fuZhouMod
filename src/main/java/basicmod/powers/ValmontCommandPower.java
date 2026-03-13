package basicmod.powers;

import basicmod.enums.CustomTags;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import basicmod.BasicMod;

public class ValmontCommandPower extends AbstractPower {
    public static final String POWER_ID = BasicMod.makeID("ValmontCommandPower");
    public static final String NAME = "老板的指使";
    public static final String[] DESCRIPTIONS = { "本回合内你的 #y葱姜蒜 、 #y特鲁 及所有 #y阿福 系列卡牌耗费变为 #b0 。" };

    public ValmontCommandPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;
        this.isTurnBased = true;

        this.loadRegion("focus");
        updateDescription();
        
        // 初始应用时立即设置手牌费用
        updateHandCosts();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void onInitialApplication() {
        updateHandCosts();
    }

    @Override
    public void onCardDraw(AbstractCard card) {
        if (shouldReduceCost(card)) {
            card.setCostForTurn(0);
        }
    }

    @Override
    public void atStartOfTurn() {
        // 虽然通常只持续一回合，但在某些情况下如果保留了，每回合开始重新应用
        updateHandCosts();
    }

    @Override
    public void onVictory() {
        // 胜利时也要清理，防止持久化问题（虽然 StS 会自动清理 Power）
    }

    private void updateHandCosts() {
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (shouldReduceCost(c)) {
                c.setCostForTurn(0);
            }
        }
    }

    private boolean shouldReduceCost(AbstractCard card) {
        // 葱姜蒜、特鲁或带有 afu 标签的卡
        return card.cardID.equals(BasicMod.makeID("WeThree")) || 
               card.cardID.equals(BasicMod.makeID("Tohru")) || 
               card.tags.contains(CustomTags.afu);
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
        }
    }
}
