package basicmod.helpers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.LinkedList;

public class MaskManager {
    public static LinkedList<String> maskHistory = new LinkedList<>();

    public static final String TALA_MASK_POWER_ID = basicmod.BasicMod.makeID("TaLaPower"); // Needs to match TaLaPower ID
    
    public static int shadowKhanCardsPlayedThisTurn = 0;
    public static int mingTaTotalDamage = 0;

    public static void clear() {
        maskHistory.clear();
        shadowKhanCardsPlayedThisTurn = 0;
        mingTaTotalDamage = 0;
    }


    public static void onMaskApplied(AbstractCreature owner, String maskPowerId, AbstractPower maskPowerInstance) {
        if (maskPowerId.equals(TALA_MASK_POWER_ID)) {
            return; // TaLa Mask is immune to eviction and doesn't count towards capacity
        }

        if (maskHistory.contains(maskPowerId)) {
            // Already in stack, order doesn't change
            return;
        }

        // New mask
        AbstractPower tala = owner.getPower(TALA_MASK_POWER_ID);
        int capacity = 2 + (tala != null ? tala.amount : 0);

        while (maskHistory.size() >= capacity) {
            String oldestId = maskHistory.removeFirst();
            AbstractPower oldestPower = owner.getPower(oldestId);
            if (oldestPower != null) {
                // Must trigger eviction
                if (oldestPower instanceof basicmod.powers.BaseMaskPower) {
                    ((basicmod.powers.BaseMaskPower) oldestPower).onEvict();
                }
                AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(owner, owner, oldestPower));
            }
        }

        maskHistory.addLast(maskPowerId);
    }

    public static void onMaskRemoved(String maskPowerId) {
        // Fallback in case a mask is removed naturally or by other means
        if (maskHistory.contains(maskPowerId)) {
            maskHistory.remove(maskPowerId);
        }
    }
}
