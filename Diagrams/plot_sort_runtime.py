import matplotlib.pyplot as plt

# =========================
# Data from current report
# Unit: milliseconds (ms)
# =========================

datasets = ["Dataset A", "Dataset B", "Dataset C"]

means = {
    "Bubble Sort": [0.240, 4.480, 0.779],
    "Quick Sort":  [1.251, 0.131, 0.567],
    "Merge Sort":  [0.274, 0.379, 0.465],
}

stds = {
    "Bubble Sort": [0.070, 0.188, 0.153],
    "Quick Sort":  [0.057, 0.037, 0.482],
    "Merge Sort":  [0.041, 0.505, 0.011],
}

# =========================
# Palette based on your reference image
# Bubble: grass green
# Quick: grey
# Merge: turquoise green
# =========================

algorithms = list(means.keys())

colors = {
    "Bubble Sort": "#D6DD55",   # grass green from reference image
    "Quick Sort":  "#616261",   # grey from reference image
    "Merge Sort":  "#54A9B9",   # turquoise green from reference image
}

# =========================
# Figure setup
# =========================

x = list(range(len(datasets)))
bar_width = 0.23

plt.rcParams["font.family"] = "DejaVu Sans"
plt.rcParams["axes.unicode_minus"] = False

fig, ax = plt.subplots(figsize=(8.4, 4.8))

# =========================
# Draw grouped bars
# =========================

for i, alg in enumerate(algorithms):
    offset = (i - 1) * bar_width
    bar_positions = [pos + offset for pos in x]

    bars = ax.bar(
        bar_positions,
        means[alg],
        width=bar_width,
        label=alg,
        color=colors[alg],
        edgecolor="#222222",
        linewidth=0.8,
    )

    # -------------------------
    # Error bars: double-layer
    # White underlay + dark overlay
    # This keeps the I-shaped caps readable on both dark bars and white background.
    # -------------------------

    ax.errorbar(
        bar_positions,
        means[alg],
        yerr=stds[alg],
        fmt="none",
        ecolor="white",
        elinewidth=3.0,
        capsize=6,
        capthick=3.0,
        zorder=4,
    )

    ax.errorbar(
        bar_positions,
        means[alg],
        yerr=stds[alg],
        fmt="none",
        ecolor="#222222",
        elinewidth=1.2,
        capsize=4.5,
        capthick=1.2,
        zorder=5,
    )

    # Mean value labels
    for bar, mean, sd in zip(bars, means[alg], stds[alg]):
        height = bar.get_height()
        ax.text(
            bar.get_x() + bar.get_width() / 2,
            height + sd + 0.08,
            f"{mean:.3f}",
            ha="center",
            va="bottom",
            fontsize=8.5,
            color="#222222",
        )

# =========================
# Axes, grid, and legend
# =========================

ax.set_xticks(x)
ax.set_xticklabels(datasets, fontsize=10.5)

ax.set_xlabel("Candidate Dataset", fontsize=11)
ax.set_ylabel("Runtime (ms, mean ± SD)", fontsize=11)

ax.set_title("Sorting Runtime Comparison", fontsize=13, pad=12)

ax.set_ylim(0, 5.25)

ax.grid(axis="y", linestyle="--", linewidth=0.7, alpha=0.32)
ax.set_axisbelow(True)

ax.spines["top"].set_visible(False)
ax.spines["right"].set_visible(False)

ax.legend(
    title="Algorithm",
    fontsize=9.5,
    title_fontsize=9.5,
    frameon=True,
    edgecolor="#DDDDDD",
    loc="upper right",
)

plt.tight_layout()

# =========================
# Export
# =========================

plt.savefig("figure4_sorting_runtime.png", dpi=300, bbox_inches="tight")
plt.savefig("figure4_sorting_runtime.svg", bbox_inches="tight")
plt.savefig("figure4_sorting_runtime.pdf", bbox_inches="tight")

plt.show()