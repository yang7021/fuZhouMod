package basicmod.relics;

import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

import static basicmod.BasicMod.makeID;

public class HorseTalisman extends BaseRelic {
    public static final String NAME = "HorseTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    private boolean removedDebuffThisCombat = false;

    public HorseTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void atPreBattle() {
        removedDebuffThisCombat = false;
    }

    @Override
    public void atTurnStart() {
        // 遍历玩家身上的所有状态
        for (AbstractPower p : AbstractDungeon.player.powers) {
            if (p.type == AbstractPower.PowerType.DEBUFF) { // 检查是否为负面状态（Debuff）
                this.flash();
                addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
                // 移除找到的第一个负面状态
                addToBot(new RemoveSpecificPowerAction(AbstractDungeon.player, AbstractDungeon.player, p));
                removedDebuffThisCombat = true; // 标记本场战斗已经触发过了清除
                break; // 每回合仅限一次，移除后即跳出循环
            }
        }
    }

    @Override
    public void onVictory() {
        // 战斗结束时，判断是否整场战斗中从未成功移除过负面状态
        if (!removedDebuffThisCombat) {
            this.flash();
            // 在战斗后回复最大生命值的20%
            AbstractDungeon.player.heal(AbstractDungeon.player.maxHealth / 5, true);
        }
        removedDebuffThisCombat = false; // 重置标记位，迎接下次战斗
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
