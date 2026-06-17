# Lvtu Master Front UI Skill

Use this guide when modifying only the administrator app in `lvtu-master-front`.

## Scope

- Only edit files under `lvtu-master-front`.
- Do not modify `lvtu-front`, `lvtu-server`, or root SQL/docs unless the task explicitly asks for it.
- Keep existing Vue 3, Vite, Pinia, Vue Router, and Element Plus conventions.

## Required Context

1. Read `lvtu-master-front/product.md`.
2. Read the root `PRODUCT.md` and `DESIGN.md` when product or visual direction is unclear.
3. For UI redesign, use the local `impeccable` skill and follow the product register.

If Codex asks you to run impeccable manually, use:

```bash
node .agents/skills/impeccable/scripts/context.mjs
```

Then follow the printed instruction. For this project it should point to the product register.

## Admin Shell

- `src/components/AdminHeader.vue` is the persistent sidebar component.
- `src/App.vue` switches between standalone auth routes and the logged-in admin shell.
- `/admin/login` should stay standalone; all other protected admin pages should sit beside the sidebar.
- Menu visibility must continue to use `ADMIN_MENU_ITEMS`, `canAccessAdminRoute`, `firstAdminPath`, and role labels from `src/utils/adminPermissions.js`.

## UI Rules

- Use system sans typography with `letter-spacing: 0`.
- Page titles should be about `26px` to `30px`, weight `800`, and not fluid.
- Body and table text should stay `14px` to `15px` with readable line height.
- Use white task surfaces on `#f5f7fb` app background.
- Use borders and dividers before shadows.
- Keep standard card radius around `8px` and large work panels around `12px`.
- Use blue only for primary actions, active navigation, links, focus states, or important values.
- Use semantic green, amber, and red only for real status or destructive actions.
- Avoid decorative gradients, glassmorphism, oversized rounded cards, and marketing-like layouts.

## Implementation Pattern

- Put shared admin styling in `src/styles/admin.css`.
- Prefer global normalization for repeated legacy classes such as `page-shell`, `content-card`, `data-table`, `modal`, and Element Plus tables.
- Only rewrite individual pages when the structure itself is wrong or a global rule cannot fix it cleanly.
- Keep local page styles scoped where they describe page-specific layout or data presentation.

## Verification

- Run `npm run build` from `lvtu-master-front` after changes.
- For significant UI changes, run the local dev server and inspect the app in the browser at the printed localhost URL.
- Check both desktop and narrow widths for sidebar behavior, table overflow, and text fitting.
