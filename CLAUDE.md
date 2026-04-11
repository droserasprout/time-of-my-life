# Project

See [docs/spec.md](docs/spec.md) for product specification
and [docs/design.md](docs/design.md) for design system.

## Linting

- `make lint` runs ktlint, detekt, and markdownlint
- `make format` auto-formats Kotlin sources with ktlint
- detekt uses a baseline file (`app/detekt-baseline.xml`); new code must pass without adding to it
