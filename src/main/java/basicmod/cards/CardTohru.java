package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardTohru extends BaseCard {
    public static final String ID = makeID("Tohru");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.RARE,
            CardTarget.ENEMY,
            2);

    public CardTohru() {
        super(ID, info);
        setDamage(8, 2);
        setBlock(8, 2);
        tags.add(CustomTags.blackhand);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 先造成基础伤害
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SMASH));

        // 捕捉未加甲前的当期护甲值，用来造成第二次伤害
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                int preBlock = p.currentBlock;
                addToTop(new DamageAction(m, new DamageInfo(p, preBlock, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                this.isDone = true;
            }
        });

        // 加上本卡的护甲
        addToBot(new GainBlockAction(p, p, this.block));
    }
}
