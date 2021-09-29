package org.starcoin.starswap.api.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.starcoin.starswap.api.data.model.NodeHeartbeat;
import org.starcoin.starswap.api.data.model.NodeHeartbeatId;

import java.util.List;

public interface NodeHeartbeatRepository extends JpaRepository<NodeHeartbeat, NodeHeartbeatId> {

    @Query(nativeQuery = true, value = "SELECT DISTINCT\n" +
            "    g.beaten_at, g.is_end_point\n" +
            "FROM\n" +
            "    (SELECT \n" +
            "        b.beaten_at AS beaten_at, TRUE AS is_end_point, b.node_id\n" +
            "    FROM\n" +
            "        node_heartbeat b\n" +
            "    LEFT JOIN node_heartbeat c ON b.node_id != c.node_id\n" +
            "        AND b.beaten_at > c.started_at\n" +
            "        AND b.beaten_at < c.beaten_at\n" +
            "    WHERE\n" +
            "        (c.node_id IS NULL)\n" +
            "            AND b.beaten_at != (SELECT \n" +
            "                MAX(b.beaten_at)\n" +
            "            FROM\n" +
            "                node_heartbeat b) " +
            "    UNION SELECT \n" +
            "        b.started_at AS beaten_at, FALSE AS is_end_point, b.node_id\n" +
            "    FROM\n" +
            "        node_heartbeat b\n" +
            "    LEFT JOIN node_heartbeat a ON b.node_id != a.node_id\n" +
            "        AND b.started_at > a.started_at\n" +
            "        AND b.started_at < a.beaten_at\n" +
            "    WHERE\n" +
            "        (a.node_id IS NULL)\n" +
            "            AND b.started_at != (SELECT \n" +
            "                MIN(b.started_at)\n" +
            "            FROM\n" +
            "                node_heartbeat b)) AS g\n" +
            "ORDER BY g.beaten_at , g.is_end_point DESC\n" +
            ";")
    List<Object[]> findBreakpoints();

}
