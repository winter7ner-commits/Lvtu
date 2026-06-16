---
name: Lvtu
description: A professional legal-service workbench for clients, lawyers, and administrators.
colors:
  primary: "#2563eb"
  primary-deep: "#1e40af"
  primary-text: "#1d4ed8"
  admin-accent: "#1d8fd6"
  surface: "#ffffff"
  app-bg: "#f5f7fb"
  panel-bg: "#f6f8fc"
  soft-blue-bg: "#eff6ff"
  soft-blue-border: "#d6e4ff"
  border: "#e5eaf3"
  divider: "#edf1f7"
  text: "#172033"
  text-secondary: "#344054"
  text-muted: "#667085"
  text-disabled: "#98a2b3"
  success: "#15803d"
  success-bg: "#f0fdf4"
  warning: "#92400e"
  warning-bg: "#fef3c7"
  danger: "#b42318"
  danger-bg: "#fff1f0"
typography:
  headline:
    fontFamily: "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Arial, sans-serif"
    fontSize: "30px"
    fontWeight: 800
    lineHeight: 1.2
    letterSpacing: "0"
  title:
    fontFamily: "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Arial, sans-serif"
    fontSize: "22px"
    fontWeight: 800
    lineHeight: 1.3
    letterSpacing: "0"
  body:
    fontFamily: "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Arial, sans-serif"
    fontSize: "14px"
    fontWeight: 400
    lineHeight: 1.6
    letterSpacing: "0"
  label:
    fontFamily: "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Arial, sans-serif"
    fontSize: "13px"
    fontWeight: 700
    lineHeight: 1.4
    letterSpacing: "0"
rounded:
  xs: "4px"
  sm: "6px"
  md: "8px"
  lg: "12px"
  xl: "16px"
  pill: "999px"
spacing:
  xs: "4px"
  sm: "8px"
  md: "12px"
  lg: "18px"
  xl: "24px"
  xxl: "32px"
components:
  button-primary:
    backgroundColor: "{colors.primary}"
    textColor: "{colors.surface}"
    rounded: "{rounded.sm}"
    padding: "10px 18px"
  button-secondary:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.primary-text}"
    rounded: "{rounded.sm}"
    padding: "9px 16px"
  card:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.text}"
    rounded: "{rounded.md}"
    padding: "22px"
  input:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.text}"
    rounded: "{rounded.sm}"
    padding: "10px 12px"
  tag-selected:
    backgroundColor: "{colors.soft-blue-bg}"
    textColor: "{colors.primary-text}"
    rounded: "{rounded.pill}"
    padding: "4px 10px"
---

# Design System: Lvtu

## 1. Overview

**Creative North Star: "The Legal Workbench"**

Lvtu should feel professional, clear, and trustworthy: a stable legal-service workbench where clients, lawyers, and administrators can understand status, complete forms, and act with confidence. The system is not expressive for its own sake. It uses alignment, borders, dividers, and a restrained blue-gray-white palette to make dense legal workflows readable.

The visual language is information-first. Creativity is subordinate to alignment, predictable grids, rigorous copy, and obvious next actions. The product should never feel playful, promotional, or entertainment-like; it should feel calm enough for sensitive legal tasks and efficient enough for repeated administrative work.

**Key Characteristics:**
- Deep blue actions on white or pale blue surfaces.
- Neutral gray backgrounds that separate pages, panels, and tables.
- Clear borders and dividers instead of decorative effects.
- Strong grid alignment, especially in forms, tables, cards, and admin views.
- Direct, precise UI copy with no emotional overstatement.

## 2. Colors

The palette is restrained: deep blue for primary action and current state, white for work surfaces, neutral gray for structure, and semantic colors only where status requires them.

### Primary
- **Workbench Blue**: Used for primary actions, selected navigation, active filters, focus rings, important counts, and current workflow state.
- **Authority Blue**: Used for app headers, high-emphasis navigation, and hover states that need stronger contrast.
- **Link Blue**: Used for inline actions, selected tags, secondary buttons, and compact administrative controls.
- **Admin Cyan Blue**: Used in older admin views as a heading/action accent. Treat it as legacy-compatible and avoid introducing new saturated cyan variants.

### Secondary
- **Success Green**: Used only for verified, approved, completed, or helpful states.
- **Warning Amber**: Used only for pending, waiting, payment, review, or caution states.
- **Danger Red**: Used only for destructive, rejected, failed, hidden, or error states.

### Neutral
- **Surface White**: The default container and form surface.
- **Application Mist**: The default page background for task screens.
- **Panel Gray**: The table header, grouped information, and quiet card background.
- **Line Gray**: The normal border for cards, tables, inputs, dividers, and panels.
- **Ink**: The primary text color for titles and important values.
- **Secondary Ink**: The body and data text color.
- **Muted Ink**: Used for helper text, labels, secondary metadata, and descriptions.

### Named Rules

**The Blue Means Action Rule.** Blue is reserved for actions, active state, focus, links, and important values. Do not use saturated blue as decoration.

**The White Surface Rule.** Task content lives on white surfaces with visible borders or dividers. Gray backgrounds separate work zones; they do not replace content panels.

**The Semantic Color Rule.** Green, amber, red, and purple-like status colors appear only when the data state requires them. Do not introduce mood colors for visual variety.

## 3. Typography

**Display Font:** System sans (with Segoe UI, Roboto, Arial fallback)
**Body Font:** System sans (with Segoe UI, Roboto, Arial fallback)
**Label/Mono Font:** System sans for labels; no dedicated mono role in the current UI.

**Character:** The typography is utilitarian and compact. It favors readable Chinese and data-heavy interfaces over brand expression.

### Hierarchy

- **Headline** (800, 28-31px, 1.2): Page titles in user and admin views.
- **Title** (800, 20-23px, 1.3): Card titles, section headers, major panel headings.
- **Body** (400-500, 14-16px, 1.6): Form copy, table cells, descriptions, and legal-service content.
- **Label** (700-800, 12-14px, 1.4): Eyebrows, field labels, badges, filter chips, and compact metadata.
- **Dense Table Text** (400-500, 14-15px, 1.45): Administrative tables where scanning matters more than prose rhythm.

### Named Rules

**The No Display Drama Rule.** Do not introduce decorative display fonts, oversized marketing headings, or expressive letter spacing. Legal-service screens need clarity, not performance.

**The Label Precision Rule.** Labels and status text must be short, literal, and stable across screens. Do not use emotional or conversational phrasing for workflow states.

## 4. Elevation

Lvtu uses a hybrid of borders and low shadows. Borders define structure at rest; shadows are shallow and used to separate panels, dropdowns, modals, or hoverable cards. Elevation should feel like a work surface lifting slightly, not a decorative card stack.

### Shadow Vocabulary

- **Header Shadow** (`box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1)`): Sticky navigation separation.
- **Panel Shadow** (`box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06)`): Primary task panels, forms, and cards.
- **Admin Panel Shadow** (`box-shadow: 0 5px 18px rgba(30, 55, 90, 0.08)`): Dense admin cards and tables.
- **Dropdown Shadow** (`box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15)`): Header dropdown menus and compact floating controls.
- **Modal Shadow** (`box-shadow: 0 24px 64px rgba(15, 23, 42, 0.22)`): Blocking dialogs and review panels.

### Named Rules

**The Border First Rule.** Use a visible border or divider before reaching for shadow. Shadow alone is not enough structure for dense legal workflows.

**The Low Lift Rule.** Hover lift may move an item by 1-2px and add a subtle blue-tinted shadow. Anything bouncy, playful, or theatrical is out of register.

## 5. Components

### Buttons

Buttons are direct, compact, and stateful.

- **Shape:** Small corners for standard buttons (4-6px), medium corners for task tiles (8-12px), pill only for tags or profile controls.
- **Primary:** Workbench Blue background with white text, usually 10px 18px or 11px 18px padding.
- **Hover / Focus:** Darken to Authority Blue or add a pale blue focus ring. Focus must remain visible under WCAG AA expectations.
- **Secondary / Ghost:** White background, blue text, blue-gray border. Use for non-destructive secondary actions.
- **Danger:** Red text or red background only for destructive actions; never mix danger with decorative hover effects.

### Chips

Chips are filters, statuses, or compact metadata.

- **Style:** Pill shape, pale blue or semantic background, high-contrast text.
- **State:** Selected chips use pale blue fill and blue text, or solid blue when selection drives a major workflow.
- **Rule:** Do not use chips as decoration; every chip should filter, classify, or communicate state.

### Cards / Containers

Cards are work containers, not marketing panels.

- **Corner Style:** 8px for admin cards and tables, 12px for user-facing list cards, 14-16px only for larger form workspaces.
- **Background:** White content surfaces on pale gray page backgrounds.
- **Shadow Strategy:** Use Panel Shadow or Admin Panel Shadow sparingly; borders remain visible.
- **Border:** Use Line Gray for all dense content cards, table containers, and form sections.
- **Internal Padding:** 18-24px for standard panels, 32-42px only for broad admin table containers.

### Inputs / Fields

Inputs are plain and predictable.

- **Style:** White or near-white background, 1px neutral border, 4-10px radius depending on surrounding density.
- **Focus:** Border shifts to Workbench Blue with a pale blue focus ring.
- **Error / Disabled:** Error copy uses red with specific, actionable wording; disabled states use muted gray and must still be legible.
- **Rule:** Do not invent custom form controls when Element Plus already provides the expected control.

### Navigation

Navigation is a stable task map.

- **Top Navigation:** Deep blue gradient header, white text, 70px desktop height, compact active states.
- **Dropdowns:** White menus with 8px radius, shallow shadow, 14px text, and strong hover contrast.
- **Side / Anchor Navigation:** Sticky, bordered, white panels with active pale blue states and numbered anchors where the order is a real workflow.
- **Mobile:** Collapse or stack structural navigation rather than shrinking text aggressively.

### Tables

Tables are the administrative backbone.

- **Headers:** Pale gray background, left-aligned, 14-19px depending on density.
- **Rows:** 1px top dividers, generous but consistent horizontal padding.
- **Content:** Long legal text should ellipsize or wrap deliberately; do not let columns break the grid.
- **Actions:** Inline action buttons should share the same 4-6px radius and semantic color vocabulary.

## 6. Do's and Don'ts

### Do:

- **Do** use deep blue, neutral gray, and white as the default palette.
- **Do** align controls, labels, and table columns to a clear grid before adding visual emphasis.
- **Do** use borders, dividers, and white surfaces to make legal workflows scannable.
- **Do** keep copy rigorous, literal, and workflow-oriented.
- **Do** preserve Element Plus conventions when they already support the expected form or table pattern.
- **Do** meet WCAG AA contrast for body text, labels, placeholders, disabled controls, focus states, and status chips.

### Don't:

- **Don't** make the UI feel entertainment-like, playful, gamified, or promotional.
- **Don't** add decorative legal cliches, ornamental gradients, glass panels, or marketing-style hero effects to task screens.
- **Don't** use emotional copy, exaggerated reassurance, or casual jokes in legal-service workflows.
- **Don't** use color for variety when the color does not carry action or state.
- **Don't** rely on shadow without borders for dense panels, cards, tables, or form sections.
- **Don't** introduce oversized radius on work cards or inputs; 4-12px is the default system range.
