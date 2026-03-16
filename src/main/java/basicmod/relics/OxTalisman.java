package basicmod.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static basicmod.BasicMod.makeID;

public class OxTalisman extends BaseRelic {
    public static final String NAME = "OxTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.HEAVY;

    public OxTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void atBattleStart() {
        this.counter = 0; // 初始化附加伤害计数器
        this.flash();
        addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        // 战斗开始时给予玩家2点力量
        addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                new StrengthPower(AbstractDungeon.player, 2), 2));
    }

    @Override
    public void onUseCard(AbstractCard targetCard, UseCardAction useCardAction) {
        // 如果打出的是攻击牌，计数器+2，这将在后续的伤害计算中作为附加伤害
        if (targetCard.type == AbstractCard.CardType.ATTACK) {
            this.counter += 2;
            this.flash();
        }
    }

    @Override
    public float atDamageModify(float damage, AbstractCard c) {
        // 在计算攻击牌基础伤害时，加上计数器中累积的额外伤害
        if (c.type == AbstractCard.CardType.ATTACK && this.counter > 0) {
            return damage + this.counter;
        }
        return damage;
    }

    @Override
    public void onVictory() {
        this.counter = -1;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
