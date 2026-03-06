package basicmod.relics;

import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import static basicmod.BasicMod.makeID;

public class RabbitTalisman extends BaseRelic {
    public static final String NAME = "RabbitTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.COMMON;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    private boolean isFirstCard = true;

    public RabbitTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void atTurnStart() {
        isFirstCard = true; // 回合开始时，标记下一张打出的是第一张牌
        this.beginPulse(); // 遗物开始脉冲闪烁，提示可以触发
        this.pulse = true;
    }

    @Override
    public void onUseCard(AbstractCard targetCard, UseCardAction useCardAction) {
        if (isFirstCard) {
            isFirstCard = false; // 标记第一张牌已打出
            this.pulse = false; // 停止闪烁提示
            this.flash();
            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));

            // 如果这张牌有正常的费用消耗（大于0），并且不是本次免费打出
            if (targetCard.costForTurn > 0 && !targetCard.freeToPlayOnce) {
                addToBot(new GainEnergyAction(targetCard.costForTurn)); // 返还相应的费用
            } else if (targetCard.cost == -1) {
                // 如果是X费用的牌，则返还本次打出时所消耗的能量
                addToBot(new GainEnergyAction(targetCard.energyOnUse));
            }
        }
    }

    @Override
    public void onVictory() {
        this.pulse = false;
        this.isFirstCard = true;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
