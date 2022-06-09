package io.github.vzwingma.finances.budget.services.communs.data.trace;

import org.slf4j.MDC;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Contexte de trace métier
 */
public class BusinessTraceContext {


    private final static BusinessTraceContext INSTANCE = new BusinessTraceContext();


    /**
     * Retourne l'instance du contexte de trace métier
     *
     * @return instance du contexte de trace métier
     */
    public static BusinessTraceContext get() {
        return INSTANCE;
    }

    /**
     * retourne l'instance après raz
     * @return instance raz
     */
    public static BusinessTraceContext getclear() {
        return get().clear();
    }

    /**
     * Contexte raz
     * @return instance raz
     */
    public BusinessTraceContext clear() {
        Arrays.stream(BusinessTraceContextKeyEnum.values())
                .forEach(key -> MDC.remove(key.getLibelle()));
        calculateBudgetContext();
        return INSTANCE;
    }
    /**
     * Ajout d'une clé métier dans les traces
     * @param key key métier
     * @param value value métier de la clé
     */
    public BusinessTraceContext put(BusinessTraceContextKeyEnum key, String value) {
        MDC.put(key.getLibelle(), value);
        calculateBudgetContext();
        return INSTANCE;
    }

    /**
     * Suppression d'une clé métier dans les traces
     * @param key clé métier
     */
    public BusinessTraceContext remove(BusinessTraceContextKeyEnum key) {
        MDC.remove(key.getLibelle());
        calculateBudgetContext();
        return INSTANCE;
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
