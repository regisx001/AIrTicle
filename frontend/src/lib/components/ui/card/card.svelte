<script lang="ts" module>
	import { tv, type VariantProps } from 'tailwind-variants';

	export const cardVariants = tv({
		base: 'rounded-lg border bg-card text-card-foreground shadow-sm',
		variants: {
			variant: {
				default: '',
				destructive:
					'border-destructive/50 text-destructive dark:border-destructive [&>svg]:text-destructive'
			}
		},
		defaultVariants: {
			variant: 'default'
		}
	});

	export type CardVariant = VariantProps<typeof cardVariants>['variant'];
</script>

<script lang="ts">
	import { cn } from '$lib/utils.js';

	let {
		class: className,
		variant = 'default',
		children,
		...restProps
	}: {
		class?: string;
		variant?: CardVariant;
		children?: any;
	} & any = $props();
</script>

<div class={cn(cardVariants({ variant }), className)} {...restProps}>
	{@render children?.()}
</div>
