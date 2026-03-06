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

    private boolean isFirstAttack = true; // 标记每回合判断是否为第一次攻击

    public PigTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void atPreBattle() {
        isFirstAttack = true; // 战斗前重置
    }

    @Override
    public void atTurnStart() {
        isFirstAttack = true; // 回合开始重置
    }

    @Override
    public void onPlayCard(AbstractCard c, com.megacrit.cardcrawl.monsters.AbstractMonster m) {
        // 如果是本回合第一次打出攻击牌
        if (isFirstAttack && c.type == AbstractCard.CardType.ATTACK) {
            isFirstAttack = false;
            this.flash(); // 遗物闪烁提示触发
            // 将该牌本回合的伤害类型改为失去生命（HP_LOSS），从而穿透防御、叠甲无效
            c.damageTypeForTurn = DamageInfo.DamageType.HP_LOSS;
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
