# Lvtu Master Front Product

## Register

product

## Surface

`lvtu-master-front` is the administrator console for Lvtu. It is used after login by super administrators, operations staff, certification auditors, and customer service staff to operate platform records, legal content, user identity review, service orders, settlements, interventions, evaluations, roles, and system settings.

## Product Purpose

The console should behave like a stable legal-service workbench. Administrators need to scan dense records, locate exceptions, audit sensitive identity information, complete platform actions, and preserve trust in order and content states. The UI should make the current module, next action, and record state obvious without decorative noise.

## Users

- Super administrators who manage all platform modules and create administrator accounts.
- Operations administrators who maintain users, legal articles, evaluations, and feedback.
- Certification auditors who review real-name and lawyer qualification applications.
- Customer service staff who query orders and handle platform intervention workflows.

## Design Direction

- Use a left sidebar as the persistent task map after login.
- Keep login as a focused standalone page without the admin shell.
- Use restrained blue, neutral gray, white surfaces, and semantic state colors only.
- Prefer dense but readable tables, bordered panels, clear section headings, and direct action buttons.
- Keep typography compact and system-native for Chinese admin workflows.

## Anti-References

- No marketing-style hero layout inside the console.
- No decorative legal imagery, glass effects, large gradients, or playful motion.
- No oversized rounded cards, floating nested cards, or inconsistent button styles.
- No color used only for variety; blue means action, active state, focus, or important value.

## Accessibility

Target WCAG AA. Body text, placeholders, disabled states, badges, and table text must remain legible. Every interactive control should have visible hover and focus states. Important status must not rely on color alone.
