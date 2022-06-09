package io.github.vzwingma.finances.budget.services.communs.data.trace;

import org.slf4j.MDC;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Contexte de trace métier
 */
public class BusinessTraceContext {


    /**
     * Ajout d'une clé métier dans les traces
     * @param key key métier
     * @param value value métier de la clé
     */
    public static void put(BusinessTraceContextKeyEnum key, String value) {
        MDC.put(key.getLibelle(), value);
        calculateBudgetContext();
    }

    /**
     * Suppression d'une clé métier dans les traces
     * @param key clé métier
     */
    public static void remove(BusinessTraceContextKeyEnum key) {
        MDC.remove(key.getLibelle());
        calculateBudgetContext();
    }

    private static void calculateBudgetContext() {
        AtomicReference<String> budgetContextValue = new AtomicReference<>("");
        MDC.getCopyOfContextMap().forEach((key1, value) -> {
            if (Arrays.stream(BusinessTraceContextKeyEnum.values())
                    .map(BusinessTraceContextKeyEnum::getLibelle).anyMatch(key -> key.equals(key1))
                    && !value.isEmpty()) {
                budgetContextValue.set(budgetContextValue.get() + "[" + key1 + ":" + value + "]");
            }
        });
        MDC.put("budgetContext", budgetContextValue.get());
    }

}
