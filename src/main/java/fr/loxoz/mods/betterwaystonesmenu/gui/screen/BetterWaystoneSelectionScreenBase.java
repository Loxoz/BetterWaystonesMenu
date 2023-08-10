package fr.loxoz.mods.betterwaystonesmenu.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.loxoz.mods.betterwaystonesmenu.compat.CText;
import fr.loxoz.mods.betterwaystonesmenu.compat.widget.TexturedButtonTooltipWidget;
import fr.loxoz.mods.betterwaystonesmenu.config.BWMSortMode;
import fr.loxoz.mods.betterwaystonesmenu.gui.widget.BetterTextFieldWidget;
import fr.loxoz.mods.betterwaystonesmenu.gui.widget.BetterWaystoneButton;
import fr.loxoz.mods.betterwaystonesmenu.gui.widget.ScrollableContainerWidget;
import fr.loxoz.mods.betterwaystonesmenu.gui.widget.TexturedEnumButtonWidget;
import fr.loxoz.mods.betterwaystonesmenu.util.query.IQueryMatcher;
import fr.loxoz.mods.betterwaystonesmenu.util.query.PartsQueryMatcher;
import fr.loxoz.mods.betterwaystonesmenu.util.WaystoneUtils;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneTypes;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.blay09.mods.waystones.network.message.RequestEditWaystoneMessage;
import net.blay09.mods.waystones.network.message.SelectWaystoneMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.Collator;
import java.util.*;
import java.util.function.Predicate;

public abstract class BetterWaystoneSelectionScreenBase extends AbstractBetterWaystoneScreen {
    public static BWMSortMode sortMode = BWMSortMode.INDEX;
    protected final List<IWaystone> waystones;
    protected Rect2i area_heading = new Rect2i(0, 0, 0, 0);
    protected Rect2i area_title = new Rect2i(0, 0, 0, 0);
    protected Rect2i area_query = new Rect2i(0, 0, 0, 0);
    protected ScrollableContainerWidget scrollable;
    protected BetterTextFieldWidget queryField;
    protected IQueryMatcher queryMatcher = new PartsQueryMatcher();
    private Screen originalScreen = null;
    private Component heading_title;
    private final List<IWaystone> visibleWaystones = new ArrayList<>();

    public BetterWaystoneSelectionScreenBase(WaystoneSelectionMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        this.waystones = container.getWaystones();
        //noinspection SuspiciousNameCombination
        imageWidth = imageHeight = CONTENT_WIDTH;
    }

    public void setOriginalScreen(Screen originalScreen) { this.originalScreen = originalScreen; }
    public Screen getOriginalScreen() { return originalScreen; }

    protected @Nullable Vec3 getOriginPos() {
        if (menu.getWaystoneFrom() != null) {
            return Vec3.atBottomCenterOf(menu.getWaystoneFrom().getPos());
        }
        if (minecraft != null && minecraft.player != null) {
            return minecraft.player.position();
        }
        return null;
    }

    protected @Nullable ResourceKey<Level> getOriginDim() {
        if (menu.getWaystoneFrom() != null) {
            return menu.getWaystoneFrom().getDimension();
        }
        if (minecraft != null && minecraft.player != null) {
            return minecraft.player.clientLevel.dimension();
        }
        return null;
    }

    protected void updateFilters() {
        visibleWaystones.clear();
        List<IWaystone> list;

        Map<IWaystone, Float> resultScores = inst().config().weightedSearch.get() ? new HashMap<>() : null;
        if (queryMatcher.isBlank()) {
            list = waystones;
        }
        else {
            Predicate<IWaystone> predicate = waystone -> queryMatcher.match(waystone.getName());
            if (resultScores != null) {
                predicate = waystone -> resultScores.compute(waystone, ($, score) -> score == null ? queryMatcher.matchScore(waystone.getName()) : score) > 0;
            }
            list = waystones.stream().filter(w -> !w.getName().isBlank()).filter(predicate).toList();
        }

        final Vec3 origin = Optional.ofNullable(getOriginPos()).orElse(new Vec3(0, 0, 0));
        Comparator<IWaystone> sortComparator = getSortComparator(sortMode, origin);
        Comparator<IWaystone> comparator = sortComparator;
        if (resultScores != null) {
            comparator = (w1, w2) -> {
                float score = (resultScores.getOrDefault(w2, 0f)) - (resultScores.getOrDefault(w1, 0f));
                if (score != 0) return score > 0 ? 1 : -1;
                return sortComparator != null ? sortComparator.compare(w1, w2) : 0;
            };
        }
        if (comparator != null) {
            list = list.stream().sorted(comparator).toList();
        }

        visibleWaystones.addAll(list);
    }

    protected int getSpecialCharWeight(String str) {
        if (str.isBlank()) return 0;
        char c = str.trim().charAt(0);
        return switch (c) {
            case '-' -> 10;
            case '=' -> 5;
            case '~' -> 2;
            default -> 0;
        };
    }

    public @Nullable Comparator<IWaystone> getSortComparator(BWMSortMode mode, Vec3 origin) {
        return switch (mode) {
            case NAME -> (w1, w2) -> {
                // put unnamed waystones at the end
                int unnamedDiff = (w2.getName().isBlank() ? -1 : 0) - (w1.getName().isBlank() ? -1 : 0);
                if (unnamedDiff != 0) return unnamedDiff;
                // allow a list of special characters to be placed at first
                if (inst().config().specialCharsFirst.get()) {
                    int specialCharWeight = getSpecialCharWeight(w2.getName()) - getSpecialCharWeight(w1.getName());
                    if (specialCharWeight != 0) return specialCharWeight;
                }
                return Collator.getInstance().compare(w1.getName(), w2.getName());
            };
            case DISTANCE -> (w1, w2) -> (int) (origin.distanceToSqr(Vec3.atBottomCenterOf(w1.getPos())) - origin.distanceToSqr(Vec3.atBottomCenterOf(w2.getPos())));
            default -> null;
        };
    }

    public void setSortMode(BWMSortMode mode) {
        sortMode = mode;
        inst().config().sortMode.set(sortMode);
        updateFilters();
        updateList();
    }

    protected boolean isIconHeading() {
        return switch (menu.getWarpMode()) {
            case WARP_STONE, WARP_SCROLL -> true;
            default -> false;
        };
    }

    protected boolean shouldShownHeading() {
        return menu.getWaystoneFrom() != null || isIconHeading();
    }

    @Override
    protected void init() {
        // content width and x excluding heading's size
        int cw = CONTENT_WIDTH;
        int cx = (width - cw) / 2;
        heading_title = menu.getWaystoneFrom() == null ? null : WaystoneUtils.getTrimmedWaystoneName(menu.getWaystoneFrom(), font, getMaxNameWidth());
        // title width (36 = space for icon and edit button)
        int title_w = heading_title != null ? (font.width(heading_title) + 36) : 0;
        // side button size
        int sbw = 20 + UI_GAP;
        // side buttons positions
        int cbw = cw + sbw * 2; // content + side buttons width
        int cbx = (width - cbw) / 2; // content + side buttons x
        // sizes that will be used as the layout
        imageWidth = Math.max(cbw, title_w);
        imageHeight = (int) (height * menuHeightScale) + (UI_GAP * 2);
        super.init();
        // compute layout
        int hw = Math.max(title_w, cw); // heading width
        int hx = (width - hw) / 2; // heading x
        int ry = topPos; // root y
        int rh = imageHeight; // root height
        int rb = ry + rh; // root bottom

        area_heading = new Rect2i(hx, ry, hw, 18);
        area_title = new Rect2i(cx, area_heading.getY() + area_heading.getHeight() + UI_GAP, cw, font.lineHeight);
        area_query = new Rect2i(cx, area_title.getY() + area_title.getHeight() + UI_GAP, cw, 20);
        // store bottom Y pos + UI_GAP of area_query
        int aq_bpos = area_query.getY() + area_query.getHeight() + UI_GAP;

        /// load sort mode
        sortMode = inst().config().sortMode.get();

        //// heading
        // rename button
        if (menu.getWaystoneFrom() != null) {
            addRenderableWidget(new TexturedButtonTooltipWidget(area_heading.getX() + area_heading.getWidth() - 18, area_heading.getY(), 18, 18, 0, 40, 18, MENU_TEXTURE, 256, 256, $ ->
                    Balm.getNetworking().sendToServer(new RequestEditWaystoneMessage(menu.getWaystoneFrom().getWaystoneUid()))
            , CText.translatable("gui.betterwaystonesmenu.waystone_selection.rename")));
        }

        //// side buttons
        int sby = area_query.getY();

        // rearrange waystones
        if (allowSorting() || allowDeletion()) {
            addRenderableWidget(new TexturedButtonTooltipWidget(cbx, sby, 20, 20, 20, 0, 20, MENU_TEXTURE, 256, 256, $ -> {
                Objects.requireNonNull(minecraft);
                minecraft.setScreen(new BetterWaystoneRearrangeScreen(menu, minecraft.player.getInventory(), this, new BetterWaystoneRearrangeScreen.Allowed(allowSorting(), allowDeletion())));
            }, CText.translatable("gui.betterwaystonesmenu.waystone_selection.rearrange")));
            sby += 20 + UI_GAP;
        }

        addRenderableWidget(new ConfigButtonWidget(cbx, sby, CText.translatable("gui.betterwaystonesmenu.waystone_selection.open_config"), inst().getConfigScreen(minecraft, this).orElse(null)));
        sby += 20 + UI_GAP;

        // return to original menu button
        addRenderableWidget(new TexturedButtonTooltipWidget(cbx, sby, 20, 20, 0, 0, 20, MENU_TEXTURE, 256, 256, $ -> {
            if (originalScreen == null) return;
            inst().openOriginalScreen(originalScreen);
        }, CText.translatable("gui.betterwaystonesmenu.waystone_selection.return_to_original")));

        //// query area
        // query field
        if (queryField == null) {
            queryField = new BetterTextFieldWidget(font, 0, 0, 100, area_query.getHeight(), CText.translatable("gui.betterwaystonesmenu.waystone_selection.query_waystones"));
            queryField.setMaxLength(128);
        }
        queryField.setPosition(area_query.getX(), area_query.getY());
        queryField.setWidth(area_query.getWidth() - 20 - UI_GAP);
        addRenderableWidget(queryField);
        if (inst().config().focusSearch.get()) {
            setInitialFocus(queryField);
        }

        // sort button
        TexturedEnumButtonWidget<BWMSortMode> sortModeBtn = new TexturedEnumButtonWidget<>(area_query.getX() + area_query.getWidth() - 20, area_query.getY(), 20, 20, BWMSortMode.values(), sortMode, mode ->
                CText.translatable("gui.betterwaystonesmenu.waystone_selection.sort_mode_prefix", CText.translatable("gui.betterwaystonesmenu.waystone_selection.sort_modes." + mode.getId()))
                , MENU_TEXTURE, 0, 92, 256, 256);
        sortModeBtn.onChange(this::setSortMode);
        addRenderableWidget(sortModeBtn);

        //// scrollbar
        if (scrollable == null) {
            scrollable = new ScrollableContainerWidget(0, 0, cw, 0);
            if (inst().config().reducedMotion.get()) {
                scrollable.setAnimated(false);
            }
        }
        scrollable.setPosition(cx, aq_bpos);
        scrollable.setHeight(rb - aq_bpos);
        addRenderableWidget(scrollable);

        //// waystone buttons
        updateFilters();
        updateList();
    }

    public void updateList() {
        scrollable.contents().clear();

        int y = 0;
        int content_h = 0;

        for (IWaystone waystone : visibleWaystones) {
            var btn = createWaystoneButton(y, waystone);
            scrollable.contents().add(btn);
            y += 20 + BTN_GAP;
            int ch = btn.y + btn.getHeight();
            if (ch > content_h) content_h = ch;
        }

        scrollable.setContentHeight(content_h);
    }

    private BetterWaystoneButton createWaystoneButton(int y, IWaystone waystone) {
        IWaystone waystoneFrom = menu.getWaystoneFrom();
        Player player = Minecraft.getInstance().player;
        int xpLevelCost = Math.round((float) PlayerWaystoneManager.predictExperienceLevelCost(Objects.requireNonNull(player), waystone, menu.getWarpMode(), waystoneFrom));
        BetterWaystoneButton btnWaystone = new BetterWaystoneButton(0, y, waystone, xpLevelCost, $ ->
                onWaystoneSelected(waystone)
        , getOriginPos(), getOriginDim());
        btnWaystone.setWidth(scrollable.getInnerWidth() - BTN_GAP);
        if (waystoneFrom != null && waystone.getWaystoneUid().equals(waystoneFrom.getWaystoneUid())) {
            btnWaystone.active = false;
        }

        return btnWaystone;
    }

    protected void onWaystoneSelected(IWaystone waystone) {
        Balm.getNetworking().sendToServer(new SelectWaystoneMessage(waystone.getWaystoneUid()));
    }

    // mouseClicked is ok (calls the widget before inventory)

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // always calling it on the scrollable container just in case
        scrollable.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    // mouseDragged patched on parent class

    // mouseScrolled can be optimized by only calling `scrollable.mouseScrolled` if in bounds

    @Override
    protected void containerTick() {
        super.containerTick();
        queryField.tick();
        if (!queryMatcher.getQuery().equals(queryField.getValue())) {
            queryMatcher.setQuery(queryField.getValue());
            updateFilters();
            updateList();
        }

        boolean reducedMotion = inst().config().reducedMotion.get();
        if (reducedMotion == scrollable.isAnimated()) {
            scrollable.setAnimated(!reducedMotion);
        }
    }

    public int getMaxNameWidth() {
        return Math.min(WAYSTONE_NAME_MAX_WIDTH, width - 36);
    }

    @Override
    public void render(@NotNull PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrices);
        // heading bg + icon
        if (shouldShownHeading()) {
            IWaystone fromWaystone = menu.getWaystoneFrom();
            boolean iconHeading = fromWaystone == null;

            int hx1 = area_heading.getX() + (!iconHeading ? 0 : (area_heading.getWidth() - 16) / 2 -1);
            int hx2 = !iconHeading ? (area_heading.getX() + area_heading.getWidth()) : (hx1 + 18);

            fill(matrices, hx1, area_heading.getY(), hx2, area_heading.getY() + area_heading.getHeight(), 0x66000000);

            if (!iconHeading) {
                drawCenteredString(matrices, font, heading_title, area_heading.getX() + area_heading.getWidth()/2, area_heading.getY() + area_heading.getHeight()/2 - font.lineHeight/2, 0xffffff);
                // icon
                RenderSystem.setShaderTexture(0, MENU_TEXTURE);
                RenderSystem.enableBlend();
                int u = 0;
                if (fromWaystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE)) u = 16;
                blit(matrices, area_heading.getX() + 1, area_heading.getY() + 1, u, 76, 16, 16);
                RenderSystem.disableBlend();
            }
            else {
                ResourceLocation item_key = switch (menu.getWarpMode()) {
                    case WARP_STONE -> new ResourceLocation("waystones", "warp_stone");
                    case WARP_SCROLL -> new ResourceLocation("waystones", "warp_scroll");
                    default -> null;
                };

                Item item = null;
                if (item_key != null) {
                    item = ForgeRegistries.ITEMS.getValue(item_key);
                }

                if (item != null) {
                    itemRenderer.blitOffset = 100.0f;
                    itemRenderer.renderAndDecorateItem(new ItemStack(item), area_heading.getX() + area_heading.getWidth()/2 - 8, area_heading.getY() + area_heading.getHeight()/2 - 8);
                    itemRenderer.blitOffset = 0f;
                }
            }
        }
        // version info
        drawVersionInfo(matrices);

        // results count
        drawCenteredString(matrices, font, CText.translatable("gui.betterwaystonesmenu.waystone_selection.showing", visibleWaystones.size(), waystones.size()), width/2, scrollable.getY() + scrollable.getHeight() + UI_GAP, 0xff737373);

        // if no waystones or results message
        if (visibleWaystones.size() == 0) {
            var message = queryMatcher.isBlank() ?
                    CText.translatable("gui.waystones.waystone_selection.no_waystones_activated").withStyle(style -> style.withColor(ChatFormatting.RED)) :
                    CText.translatable("gui.betterwaystonesmenu.waystone_selection.no_results").withStyle(style -> style.withColor(ChatFormatting.GRAY));
            drawCenteredString(matrices, font, message, scrollable.getX() + scrollable.getWidth()/2, (scrollable.getY() + scrollable.getHeight()/2) - (font.lineHeight/2), 0xffffff);
        }
        matrices.popPose();
        // widgets and labels
        super.render(matrices, mouseX, mouseY, partialTicks);
        // container slot's tooltip (unused for this menu)
        renderTooltip(matrices, mouseX, mouseY);
        // tooltips
        renderChildrenTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@NotNull PoseStack matrices, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        matrices.pushPose();
        matrices.translate(-leftPos, -topPos, 0);
        drawCenteredString(matrices, font, getTitle().copy().withStyle(style -> style.withColor(ChatFormatting.GRAY)), area_title.getX() + area_title.getWidth()/2, area_title.getY(), 0xffffff);
        matrices.popPose();
    }

    protected boolean allowSorting() { return true; }
    protected boolean allowDeletion() { return true; }

    protected class ConfigButtonWidget extends TexturedButtonTooltipWidget {
        public ConfigButtonWidget(int x, int y, Component message, Screen configScreen) {
            super(x, y, 20, 20, 60, 0, 20, MENU_TEXTURE, 256, 256, $ -> {
                if (configScreen == null) return;
                //noinspection ConstantConditions
                minecraft.setScreen(configScreen);
            }, message);
            if (configScreen == null) active = false;
        }

        @Override
        public void renderButton(@NotNull PoseStack matrices, int mouseX, int mouseY, float delta) {
            super.renderButton(matrices, mouseX, mouseY, delta);
            if (!active) fill(matrices, x, y, x + getWidth(), y + getHeight(), 0xcc141414);
        }

        @Override
        public List<Component> getTooltip() {
            var list = new ArrayList<>(super.getTooltip());
            if (!active) list.add(CText.translatable("gui.betterwaystonesmenu.waystone_selection.config_requires_configured"));
            return Collections.unmodifiableList(list);
        }
    }
}
