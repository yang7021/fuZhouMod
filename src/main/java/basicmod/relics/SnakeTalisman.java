package basicmod.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;

import static basicmod.BasicMod.makeID;

public class SnakeTalisman extends BaseRelic {
    public static final String NAME = "SnakeTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    private boolean playedAttackThisTurn = false;

    public SnakeTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void atTurnStart() {
        playedAttackThisTurn = false; // 回合开始，重置打出过攻击牌的标记
        this.pulse = true; // 遗物发光，提示回合开始时处于可触发状态
        this.beginPulse();
    }

    @Override
    public void onUseCard(AbstractCard targetCard, UseCardAction useCardAction) {
        if (targetCard.type == AbstractCard.CardType.ATTACK) {
            playedAttackThisTurn = true; // 记录本回合打出过攻击牌
            this.pulse = false; // 如果打出了攻击牌，结束闪烁提示
        }
    }

    @Override
    public void onPlayerEndTurn() {
        if (!playedAttackThisTurn) { // 如果本回合内没有打出过攻击牌
            this.flash();
            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            // 给予玩家1层“无实体”buff，可让受到的所有单次伤害变为1
            addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                    new IntangiblePlayerPower(AbstractDungeon.player, 1), 1));
        }
        this.pulse = false; // 回合结束，关闭脉冲发光
    }

    @Override
    public void onVictory() {
        playedAttackThisTurn = false;
        this.pulse = false;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
