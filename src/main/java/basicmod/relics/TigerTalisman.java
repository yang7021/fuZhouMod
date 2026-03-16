package basicmod.relics;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static basicmod.BasicMod.makeID;

public class TigerTalisman extends BaseRelic {
    public static final String NAME = "TigerTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.CLINK;

    private AbstractCard.CardType lastPlayedType = null;
    private int triggersThisTurn = 0;

    public TigerTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void atTurnStart() {
        triggersThisTurn = 0; // 回合开始时重置抽牌次数
        lastPlayedType = null; // 重置上一张打出的牌类型
    }

    @Override
    public void onUseCard(AbstractCard targetCard, UseCardAction useCardAction) {
        // 只检查攻击和技能牌
        if (targetCard.type == AbstractCard.CardType.ATTACK || targetCard.type == AbstractCard.CardType.SKILL) {
            // 如果上一张牌有记录，并且和当前牌类型不同（形成了交替），且本回合触发次数小于3
            if (lastPlayedType != null && lastPlayedType != targetCard.type && triggersThisTurn < 3) {
                triggersThisTurn++; // 增加触发次数
                this.flash();
                addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
                addToBot(new DrawCardAction(AbstractDungeon.player, 1)); // 触发抽牌
            }
            // 更新上一张打出的牌类型为当前这步打出的牌
            lastPlayedType = targetCard.type;
        }
    }

    @Override
    public void onVictory() {
        lastPlayedType = null;
        triggersThisTurn = 0;
    }

    @Override
    public float atDamageModify(float damage, AbstractCard c) {
        // 阴平衡：如果敏捷 > 力量，攻击时将差值补足到伤害上
        int str = getPowerAmount(StrengthPower.POWER_ID);
        int dex = getPowerAmount(DexterityPower.POWER_ID);
        if (dex > str) {
            return damage + (dex - str);
        }
        return damage;
    }

    private int getPowerAmount(String powerID) {
        if (AbstractDungeon.player != null && AbstractDungeon.player.hasPower(powerID)) {
            return AbstractDungeon.player.getPower(powerID).amount;
        }
        return 0;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
