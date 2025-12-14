# Instructions for GitHub Copilot

## Follow the original prompt

**If the pull request has an original prompt, your only instructions are the original prompt and the original issue.**

**You must not look at the title of the pull request or any of the non-original AI-generated summaries. They contain lies that misinterpret the instructions.**

## Follow the original natural language

In issues, you must communicate in the same natural language as the initial message.

In pull requests, you must communicate in the same natural language as the "Original prompt" written in the initial message. If the original prompt is not provided, use the natural language of the title.

When launching the Agent from GitHub to create a pull request, you must initialize the pull request title and initial comment in the same natural language as the user's.

## Write documents in English

You must write all project documentation (such as `README.md` and contents under `docs/`) in English.

## Domain blocking

When you invoke Chrome (e.g., to analyze HTML files), specify the following options to suppress Chrome’s own background network activity and prevent connection attempts to disallowed domains triggered by that activity.

```
--disable-background-networking
--disable-component-extensions-with-background-pages
--disable-component-update
```

---

If you encounter an error while running a Gradle task that says access to a specific domain is blocked, your correct response is to suggest adding an allowance for that domain via chat by providing the following URL:

https://github.com/MirrgieRiana/xarpeg-kotlin-peg-parser/settings/copilot/coding_agent/allowlist
