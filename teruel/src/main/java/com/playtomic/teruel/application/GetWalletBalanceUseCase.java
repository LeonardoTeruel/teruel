package com.playtomic.teruel.application;

import java.math.BigDecimal;
import java.util.UUID;

public interface GetWalletBalanceUseCase {

    BigDecimal getWalletBalance(UUID userId);
}
