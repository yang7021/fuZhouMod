package basicmod.relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageRandomEnemyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static basicmod.BasicMod.makeID;

public class PigTalisman extends BaseRelic {
    public static final String NAME = "PigTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.COMMON;
    private static final LandingSound SOUND = LandingSound.CLINK;

    public PigTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void onCardDraw(AbstractCard drawnCard) {
        this.flash(); // 遗物闪烁提示触发
        // 对随机敌人造成3点荆棘类型的伤害，并带有横向斩击特效
        addToBot(new DamageRandomEnemyAction(
                new DamageInfo(AbstractDungeon.player, 3, DamageInfo.DamageType.THORNS),
                AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
