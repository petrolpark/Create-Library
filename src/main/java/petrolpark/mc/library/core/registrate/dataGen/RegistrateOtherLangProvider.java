package petrolpark.mc.library.core.registrate.dataGen;

import java.util.function.Supplier;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;

import net.minecraft.data.PackOutput;

public class RegistrateOtherLangProvider extends RegistrateLangProvider {
    
    protected final AbstractRegistrate<?> owner;
    protected final Supplier<ProviderType<RegistrateOtherLangProvider>> providerTypeSup;

    public RegistrateOtherLangProvider(AbstractRegistrate<?> owner, PackOutput packOutput, Supplier<ProviderType<RegistrateOtherLangProvider>> providerTypeSup) {
        super(owner, packOutput);
        this.owner = owner;
        this.providerTypeSup = providerTypeSup;
    };

    @Override
    protected void addTranslations() {
        owner.genData(providerTypeSup.get(), this);
    };
};
